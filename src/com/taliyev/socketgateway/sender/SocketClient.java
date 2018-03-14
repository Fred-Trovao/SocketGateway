package com.taliyev.socketgateway.sender;

import com.taliyev.socketgateway.exception.SocketGatewayException;
import com.taliyev.socketgateway.util.Client;
import com.taliyev.socketgateway.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

/*
 * Author: toghrul
 * Date: 08/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public class SocketClient {

    private Client clientParams;
    private Logger logger;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String startCommand;

    public SocketClient(Client clientParams) {
        this.clientParams = clientParams;
        initialize();
    }

    private void initialize() {
        logger = LogManager.getLogger(SocketClient.class);
    }

    // Connect to server parameters
    public void connect() {
        try {
            logger.info("Try to connect " + clientParams);
            socket = new Socket(clientParams.getHost(), clientParams.getPort());
            socket.setKeepAlive(true);
            socket.setSoTimeout(30000);
            setOutputStream();
            setInputStream();
            info("Connected to " + clientParams);
        } catch (IOException ex) {
            error("Can't connect to host", ex);
        }
    }

    // Send command with response
    public String sendCommandWithResponse(String command) throws SocketGatewayException {
        sendCommand(command);
        try {
            return readResponse();
        } catch (SocketGatewayException ex) {
            logger.error("Connection closed by server side...", ex);
            throw ex;
        }
    }

    // Send command
    public void sendCommand(String command) {
        if (command == null) {
            info("Command is null. Will not be sending to client...");
            return;
        }

        try {
            debug("Command will be sent >>> " + command);
            writer.write(command + Constants.NEW_LINE);
            writer.flush();
        } catch (IOException ex) {
            error("Error while sending command", ex);
        }
    }

    // Read response
    public String readResponse() throws SocketGatewayException {
        String response = null;
        try {
            response = reader.readLine();
            if (response != null) {
                response = response.replaceFirst("Enter command:", "").trim();
            } else {
                closeSocket();
                throw new SocketGatewayException("Unfortunately connection closed by server side...");
            }
            debug("Response received from server <<< " + response);
        } catch (IOException ex) {
            error("Error while getting response from server...", ex);
        }

        return response;
    }

    // Read till end
    public String readTillEnd() {
        StringBuilder builder = new StringBuilder();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            debug("Read till end of reader... " + builder);
        } catch (IOException ex) {
            error("Error while getting response from server...", ex);
        }

        return builder.toString();
    }

    // Wait till welcome message end
    public String getWelcomeMessage() throws SocketGatewayException {
        if (startCommand == null) {
            info("Start message is null. Application can't wait till start message");
            return null;
        }

        info("Waiting welcome message: " + startCommand);
        StringBuilder builder = new StringBuilder();
        String response = "";
        while (!response.toLowerCase().equalsIgnoreCase(startCommand.toLowerCase())) {
            response = readResponse();
            builder.append(response).append(Constants.NEW_LINE);
        }

        return builder.toString();
    }

    // Ping
    public void ping() {
        info("Ping command send to: " + clientParams);
        sendCommand("");
    }

    public Client getClientParams() {
        return clientParams;
    }

    public void setClientParams(Client clientParams) {
        this.clientParams = clientParams;
    }

    // Sets the input stream for the client socket
    private void setInputStream() {
        if (socket != null) {
            try {
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException ex) {
                error("Error while setting reader...", ex);
                closeSocket();
            }
        }
    }

    // Sets the output stream for the client socket
    private void setOutputStream() {
        if (socket != null) {
            try {
                this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException ex) {
                error("Error while setting writer...", ex);
                closeSocket();
            }
        }
    }

    // Close socket
    public void closeSocket() {
        info("Close command called... Trying to close socket, reader & writer... ");
        try {
            socket.close();
            closeReaderAndWriter();
        } catch (IOException ex) {
            error("Error while closing socket...", ex);
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
            error("Error while closing clients' writer & reader", ex);
        }
    }

    public String getStartCommand() {
        return startCommand;
    }

    public void setStartCommand(String startCommand) {
        this.startCommand = startCommand;
    }

    private void debug(String log) {
        logger.debug(getClientParams().getDescription() + " : " + log);
    }

    private void info(String log) {
        logger.info(getClientParams().getDescription() + " : " + log);
    }

    private void error(String log, IOException ex) {
        logger.error(getClientParams().getDescription() + " : " + log, ex);
    }

    public boolean checkConnectionStatus() {
        return (socket == null) ? false : socket.isConnected();
    }

    public boolean checkBound() {
        return (socket == null) ? false : socket.isBound();
    }

    public boolean checkClosed() {
        return (socket == null) ? false : socket.isClosed();
    }

}
