package com.taliyev.socketgateway;

import com.taliyev.socketgateway.listener.SocketListener;
import com.taliyev.socketgateway.util.*;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

/**
 * Author: Toghrul Aliyev
 * Email: togrul88@gmail.com
 * Date: 07.01.2018
 */
public class SocketGateway {

    private final static String CONFIG_FILE = "config.xml";
    private static Logger logger;
    private static SocketListener socketListener;


    public static void main(String[] args) {
        initilization();
        addShutdownHookHandler();
    }

    // Initialization when start application
    private static void initilization() {
        initializeLog();
        readConfigurationFile(CONFIG_FILE);
        initializeSocketListener();
    }

    // Initialize log
    private static void initializeLog() {
        logger = LogManager.getLogger(SocketGateway.class);
        logger.info("Logger initialized...");
    }

    // Read configuration xml file
    private static void readConfigurationFile(String file) {
        Configurations configurations = new Configurations();
        try {
            logger.info("Read main configuration file: " + file);
            XMLConfiguration xmlConfiguration = configurations.xml(file);

            ServerParams.setPort(xmlConfiguration.getString(ConstantsEnum.SERVER_PORT.getValue()));
            ServerParams.setMaxAcceptedConnections(xmlConfiguration.getString(ConstantsEnum.MAX_ACCEPTED_CONNECTIONS.getValue()));
            ServerParams.setConnectionTimeout(xmlConfiguration.getString(ConstantsEnum.CONNECTION_TIMEOUT.getValue()));

            readDatabaseParameters(xmlConfiguration);

            String[] clientsHost = xmlConfiguration.getStringArray(ConstantsEnum.CLIENT_HOST.getValue());
            String[] clientsPort = xmlConfiguration.getStringArray(ConstantsEnum.CLIENT_PORT.getValue());
            String[] clientsDesc = xmlConfiguration.getStringArray(ConstantsEnum.CLIENT_DESC.getValue());
            String[] clientsWelcomeMessages = xmlConfiguration.getStringArray(ConstantsEnum.CLIENT_WELCOME_MESSAGE.getValue());

            for (int i = 0; i < clientsHost.length; i++) {
                if (clientsHost[i] != null && clientsPort.length > i && clientsPort[i] != null) {
                    Client client = new Client(clientsHost[i], clientsPort[i], clientsDesc[i]);
                    client.setWelcomeMessage(clientsWelcomeMessages[i]);
                    ServerParams.addClient(client);
                }
            }

            logger.info("Server params configured: " + new ServerParams());
        } catch (ConfigurationException ex) {
            logger.error("Error while reading main configuration file", ex);
            ex.printStackTrace();
        }
    }

    // Read database parameters
    private static void readDatabaseParameters(XMLConfiguration xmlConfiguration) {
        logger.info("Read database parameters from configuration");

        if (xmlConfiguration.getKeys(ConstantsEnum.DB_CONFIGURATUION.getValue()).hasNext()) {
            logger.info("Database parameters found. Start to read parameters...");
            DBParams dbParams = new DBParams();

            dbParams.setDatabaseType(xmlConfiguration.getString(ConstantsEnum.DB_TYPE.getValue()));
            dbParams.setUsername(xmlConfiguration.getString(ConstantsEnum.DB_USERNAME.getValue()));
            dbParams.setPassword(xmlConfiguration.getString(ConstantsEnum.DB_PASSWORD.getValue()));
            dbParams.setHost(xmlConfiguration.getString(ConstantsEnum.DB_HOST.getValue()));
            dbParams.setPort(CastingOperations.parseInt(xmlConfiguration.getString(ConstantsEnum.DB_PORT.getValue())));
            dbParams.setDatabaseName(xmlConfiguration.getString(ConstantsEnum.DB_NAME.getValue()));

            logger.info("DBParams ready... " + dbParams);
            ServerParams.setDbParams(dbParams);
        } else {
            ServerParams.setDbParams(null);
            logger.info("Database parameters NOT found. Application will be work without db...");
        }
    }

    // Initialize socket listener
    private static void initializeSocketListener() {
        socketListener = new SocketListener();
        //try {Thread.sleep(10000);} catch (Exception e){}
        //socketListener.stop();
    }

    private static void addShutdownHookHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shutdown hook handler handled... ");
                if (socketListener != null){
                    socketListener.stop();
                }
            }
        });
    }
}

