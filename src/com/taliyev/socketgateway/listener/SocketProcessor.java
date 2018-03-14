package com.taliyev.socketgateway.listener;

import com.taliyev.socketgateway.db.mysql.MySQLConnection;
import com.taliyev.socketgateway.exception.SocketGatewayException;
import com.taliyev.socketgateway.sender.SocketClient;
import com.taliyev.socketgateway.util.Client;
import com.taliyev.socketgateway.util.Constants;
import com.taliyev.socketgateway.util.ServerParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Author: toghrul
 * Date: 07/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public class SocketProcessor implements Runnable {

    private Logger logger;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private List<SocketClient> socketClientList;
    private final String MAIN_NODE = "EMA15";
    private MySQLConnection dbConnection;

    SocketProcessor(Socket socket) {
        logger = LogManager.getLogger(SocketProcessor.class);
        this.socket = socket;
        setInputStream();
        setOutputStream();
        if (ServerParams.isDbEnabled()) {
            dbConnection = new MySQLConnection();
        }
    }

    @Override
    public void run() {
        if (socket == null) {
            logger.error("Socket is null, so process not starts...");
            return;
        }

        welcomeMessage();
        connectToClients();
        message();
    }

    // Send welcome message to client
    private void welcomeMessage() {
        send("Welcome to SocketGateway. ", true, false);
        send("This application can send commands to multiple instances.", true, false);
        send("This messages generated automatically. Next messages will be redirected from main server instance. Good luck...", true, false);
        send(" ", true, false);
    }

    // Connect to clients
    private void connectToClients() {
        List<Client> clientList = ServerParams.getClientList();
        if (socketClientList == null) {
            socketClientList = new ArrayList<>();
        }
        for (Client client : clientList) {
            SocketClient socketClient = new SocketClient(client);
            socketClient.setStartCommand(client.getWelcomeMessage());
            socketClient.connect();

            socketClientList.add(socketClient);
        }
        String mainNodeWelcomeMessage = getMainNodeWelcomeMessage();
        send(mainNodeWelcomeMessage, true, true);

        for (int i = 0; i < 2; i++) {
            sendCommandsToServers("", false, false, null);
        }
    }

    // Get main node welcome message
    private String getMainNodeWelcomeMessage() {
        String welcomeMsg = "";
        for (SocketClient socketClient : socketClientList) {
            String s = null;
            try {
                s = socketClient.getWelcomeMessage();
            } catch (SocketGatewayException ex) {
                logger.error(socketClient.getClientParams().getDescription(), ex);
                close();
                break;
            }
            if (socketClient.getClientParams().getDescription().equals(MAIN_NODE)) {
                welcomeMsg = s;
            }
        }

        return welcomeMsg;
    }

    // Send message to client
    private void send(String message, boolean newLine, boolean prepareNewCommandPrompt) {
        if (message == null) {
            logger.info("Message is null. Will not be sending to client...");
            return;
        }

        try {
            logger.info("Is send enter command " + prepareNewCommandPrompt + ", original message is " + message);
            writer.flush();
            if (newLine) {
                message += Constants.NEW_LINE;
            }
            writer.write(message);
            writer.flush();
            if (prepareNewCommandPrompt) {
                writer.write("Enter command:");
                writer.flush();
            }
            logger.debug("Command sent to client >>> " + message);
        } catch (IOException ex) {
            logger.error("Error while sending message to client...", ex);
            close();
        }
    }

    // Read message from client
    private void message() {
        try {
            Date commandAcceptedTime = new Date();
            String command = reader.readLine().trim();
            logger.debug("Command receive from client >>> " + command);
            if (!command.trim().isEmpty()) {
                switch (command.toUpperCase()) {
                    case Constants.LOGOUT:
                        closeAllClients(commandAcceptedTime, command);
                        close();
                        break;
                    default:
                        sendCommandsToServers(command, true, true, commandAcceptedTime);
                }
            } else {
                pingServers();
            }
        } catch (IOException ex) {
            logger.error("Error while getting message from client...", ex);
            close();
        }
    }

    // Send received command to servers
    private void sendCommandsToServers(String command, boolean isContinue, boolean isReturnResponse, Date commandAcceptedTime) {
        logger.debug("Send commands to servers... >>> " + command);

        Integer requestId = -1;
        if (command != null && command.length() > 0) {
            requestId = getRequestId();
        }

        String responseToClient = "";
        for (SocketClient socketClient : socketClientList) {
            Date beforeTime = new Date();
            String response = null;
            try {
                response = socketClient.sendCommandWithResponse(command);
            } catch (SocketGatewayException ex) {
                logger.error(socketClient.getClientParams().getDescription(), ex);
                close();
                break;
            }
            if (socketClient.getClientParams().getDescription().equalsIgnoreCase(MAIN_NODE)) {
                responseToClient = response;
            }
            registerResponseCommand(socketClient.getClientParams().getDescription(), requestId, response, beforeTime, new Date());
        }

        if (isReturnResponse) {
            send(responseToClient, true, true);
            registerEnteredCommand(requestId, command, commandAcceptedTime, new Date(), responseToClient);
        }

        if (isContinue) {
            message();
        }
    }

    // Read from servers
    private String readResponsesFromServers() {
        logger.debug("Read responses from servers... Server size: " + socketClientList.size());
        String msg = "";
        for (SocketClient socketClient : socketClientList) {
            String s = null;
            try {
                s = socketClient.readResponse();
            } catch (SocketGatewayException ex) {
                logger.error(socketClient.getClientParams().getDescription(), ex);
                close();
                break;
            }
            logger.debug("Response from " + socketClient.getClientParams().getDescription() + " is " + s);
            if (socketClient.getClientParams().getDescription().equals(MAIN_NODE)) {
                msg = s;
            }
        }
        logger.debug("Main client response is " + msg);
        return msg;
    }

    // Ping servers
    private void pingServers() {
        logger.debug("Ping servers... Server size: " + socketClientList.size());
        for (SocketClient socketClient : socketClientList) {
            socketClient.ping();
        }
    }

    // Close all clients
    private void closeAllClients(Date commandAcceptTime, String command) {
        logger.debug("Closing client sockets...");
        Integer requestId = getRequestId();
        registerEnteredCommand(requestId, command, commandAcceptTime, new Date(), "...");
        for (SocketClient socketClient : socketClientList) {
            socketClient.closeSocket();
            registerResponseCommand(socketClient.getClientParams().getDescription(), requestId, "...", commandAcceptTime, new Date());
        }
    }


    public Socket getSocket() {
        return socket;
    }

    // Sets the input stream for the client socket
    private void setInputStream() {
        if (socket != null) {
            try {
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException ex) {
                logger.error("Error while setting reader...", ex);
                close();
            }
        }
    }

    // Sets the output stream for the client socket
    private void setOutputStream() {
        if (socket != null) {
            try {
                this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException ex) {
                logger.error("Error while setting writer...", ex);
                close();
            }
        }
    }

    // Close socket
    private void close() {
        logger.info("Close command called... Trying to close socket, reader & writer... ");
        try {
            closeAllClients(new Date(), "CLOSE_FROM_APP;");
            socket.close();
            closeReaderAndWriter();
        } catch (IOException ex) {
            logger.error("Error while closing socket...", ex);
        }
    }

    // Close reader & writer
    private void closeReaderAndWriter() {
        try {
            if (writer != null) {
                writer.close();
                writer = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
        } catch (IOException ex) {
            logger.error("Error while closing writer & reader", ex);
        }
    }

    // Get DB request id
    private Integer getRequestId() {
        Integer id = null;
        if (!ServerParams.isDbEnabled()) {
            logger.trace("DB is not enabled. Skip logging in DB ... ");
            return id;
        }

        if (dbConnection == null) {
            dbConnection = new MySQLConnection();
        }

        try {
            id = dbConnection.getNextRequestId();
            logger.debug("Got request ID from DB: " + id);
        } catch (SQLException ex) {
            logger.error("Error while getting request id from db", ex);
            id = -1;
        }

        return id;
    }

    // Register request in DB
    private void registerEnteredCommand(Integer id, String command, Date startTime, Date finishTime, String response) {
        if (!ServerParams.isDbEnabled()) {
            logger.trace("DB is not enabled. Skip logging in DB ... ");
            return;
        }

        if (id < 0) {
            logger.info("Can't got correct request id from DB. Skip...");
            return;
        }

        if (dbConnection == null) {
            dbConnection = new MySQLConnection();
        }

        try {
            dbConnection.insert(id, command, startTime, finishTime, response, "Success");
        } catch (SQLException ex) {
            logger.error("Error while inserting request into db", ex);
        }
    }

    // Register response in DB
    private void registerResponseCommand(String nodeType, Integer requestId,
                                         String response, Date startTime, Date finishTime) {
        if (!ServerParams.isDbEnabled()) {
            logger.trace("DB is not enabled. Skip logging in DB ... ");
            return;
        }

        if (requestId < 0) {
            logger.info("Can't got correct request id from DB. Skip...");
            return;
        }


        if (dbConnection == null) {
            dbConnection = new MySQLConnection();
        }

        try {
            dbConnection.registerResponse(nodeType, null, requestId, response, startTime, finishTime);
        } catch (SQLException ex) {
            logger.error("Error while inserting response into db", ex);
        }
    }
}
