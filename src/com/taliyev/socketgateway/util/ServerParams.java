package com.taliyev.socketgateway.util;

import java.util.ArrayList;
import java.util.List;

public class ServerParams {

    private static Integer port;
    private static Integer maxAcceptedConnections;
    private static Integer connectionTimeout;
    private static List<Client> clientList;
    private static DBParams dbParams;

    public static Integer getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        ServerParams.port = port;
    }

    public static void setPort(String port) {
        setPort(CastingOperations.parseInt(port));
    }

    public static Integer getMaxAcceptedConnections() {
        return maxAcceptedConnections;
    }

    public static void setMaxAcceptedConnections(Integer maxAcceptedConnections) {
        ServerParams.maxAcceptedConnections = maxAcceptedConnections;
    }

    public static void setMaxAcceptedConnections(String maxAcceptedConnections) {
        setMaxAcceptedConnections(CastingOperations.parseInt(maxAcceptedConnections));
    }

    public static Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public static void setConnectionTimeout(Integer connectionTimeout) {
        ServerParams.connectionTimeout = connectionTimeout;
    }

    public static void setConnectionTimeout(String connectionTimeout) {
        setConnectionTimeout(CastingOperations.parseInt(connectionTimeout));
    }

    public static List<Client> getClientList() {
        return clientList;
    }

    public static void setClientList(List<Client> clientList) {
        ServerParams.clientList = clientList;
    }

    public static void addClient(Client client) {
        if (clientList == null) {
            clientList = new ArrayList<Client>();
        }

        clientList.add(client);
    }

    public static DBParams getDbParams() {
        return dbParams;
    }

    public static boolean isDbEnabled() {
        return (dbParams == null) ? false : true;
    }

    public static void setDbParams(DBParams dbParams) {
        ServerParams.dbParams = dbParams;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ServerParams{");
        sb.append("serverPort='").append(getPort()).append('\'');
        sb.append(", maxAcceptedConnections=").append(getMaxAcceptedConnections());
        sb.append(", connectionTimeout=").append(getConnectionTimeout());
        sb.append(", clientList=").append(getClientList());
        sb.append('}');
        return sb.toString();
    }

}
