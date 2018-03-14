package com.taliyev.socketgateway.listener;

import com.taliyev.socketgateway.util.ServerParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
 * Author: toghrul
 * Date: 07/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public class SocketListener {

    private static Logger logger;
    private ServerSocket serverSocket;
    private boolean isContinueWorking = true;
    private List<SocketProcessor> socketList;

    public SocketListener() {
        initialize();
    }

    // Initialize log & open port for listening
    private void initialize() {
        logger = LogManager.getLogger(SocketListener.class);
        socketList = new ArrayList<>();
        startListeningPort();
        acceptConnections();
    }

    // Open port for accepting tcp sockets
    private void startListeningPort() {
        try {
            serverSocket = new ServerSocket(ServerParams.getPort());
            logger.info("Server listening port: " + ServerParams.getPort());
        } catch (IOException ioException) {
            logger.fatal("Can't listen port", ioException);
        }
    }

    // Accept connections
    private void acceptConnections() {
        logger.info("Starting to accept connections from clients ... ");
        while (isContinueWorking) {
            try {
                Socket socket = serverSocket.accept();
                logger.info("Socket accepted... " + socket);
                if (ServerParams.getMaxAcceptedConnections() == 0 ||
                        (ServerParams.getMaxAcceptedConnections() - socketList.size() > 1)) {
                    processSocket(socket);
                } else {
                    logger.info("Sockets reached max connection count... " +
                            "New request rejected... " +
                            "Max connection: " + ServerParams.getMaxAcceptedConnections() +
                            ", Current connection count: " + socketList.size());
                    closeSocket(socket);
                }
            } catch (IOException e) {
                logger.error("Error while accepting socket...", e);
            }
        }
    }

    // Process accepted connections
    private void processSocket(Socket socket) {
        SocketProcessor socketProcessor = new SocketProcessor(socket);
        socketList.add(socketProcessor);
        socketProcessor.run();
    }

    // Stop working
    public void stop() {
        logger.info("Stop command accepted... Trying to close listener...");
        isContinueWorking = false;
        try {
            closeSockets();
            stopListening();
            logger.info("Stop command executed successfully");
        } catch (IOException ex) {
            logger.error("Stop command finished with FAIL", ex);
        }
    }

    // Close listener
    private void stopListening() throws IOException {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                throw new IOException(ex);
            }
        }
    }

    // Close sockets
    private void closeSockets() {
        logger.info("Trying to close accepted sockets... Socket count: " + socketList.size());
        for (SocketProcessor processor : socketList) {
            closeSocket(processor.getSocket());
        }
    }

    // Close socket
    private void closeSocket(Socket socket) {
        if (socket != null) {
            logger.info("Checking socket: " + socket);
            logger.info("Socket is connected? - " + socket.isConnected());
            if (socket.isConnected()) {
                logger.info("Closing socket ... " + socket);
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.error("Error while closing socket..." + socket, ex);
                }
            }
        }
    }
}
