package com.taliyev.socketgateway.util;

/*
 * Author: toghrul
 * Date: 11/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public class DBParams {

    private static String username;
    private static String password;
    private static String host;
    private static Integer port;
    private static String databaseName;
    private static String databaseType;

    public DBParams() {}

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DBParams.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DBParams.password = password;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        DBParams.host = host;
    }

    public static Integer getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        DBParams.port = port;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static void setDatabaseName(String databaseName) {
        DBParams.databaseName = databaseName;
    }

    public static String getDatabaseType() {
        return databaseType;
    }

    public static void setDatabaseType(String databaseType) {
        DBParams.databaseType = databaseType;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer("DBParams{");
        sb.append("databaseType='").append(databaseType).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append("***").append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", port=").append(port);
        sb.append(", databaseName='").append(databaseName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
