package com.taliyev.socketgateway.db.mysql;

import com.taliyev.socketgateway.util.DBParams;
import com.taliyev.socketgateway.util.ServerParams;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/*
 * Author: toghrul
 * Date: 11/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public final class DBConnection {

    private static final BasicDataSource connectionPool = new BasicDataSource();
    private static Logger logger = LogManager.getLogger(DBConnection.class);
    private static final String UTILITY_NAME = "com.mysql.jdbc.Driver";

    public DBConnection() {
        initialize();
    }

    public static void initialize() {
        logger.info("Initialize DB connection: " + ServerParams.getDbParams());
        connectionPool.setDriverClassName(UTILITY_NAME);
        connectionPool.setUsername(ServerParams.getDbParams().getUsername());
        connectionPool.setPassword(ServerParams.getDbParams().getPassword());
        connectionPool.setUrl(buildDbUrl());
        connectionPool.setMaxWaitMillis(30000);
        connectionPool.setMaxIdle(50);
    }

    private static String buildDbUrl() {
        StringBuilder builder = new StringBuilder("jdbc:mysql://");
        builder.append(ServerParams.getDbParams().getHost()).append(":");
        builder.append(ServerParams.getDbParams().getPort()).append("/");
        builder.append(ServerParams.getDbParams().getDatabaseName()).append("?");
        builder.append("autoReconnect=true").append("&");
        builder.append("useSSL=false");

        logger.info("JDBC Url: " + builder.toString());
        return builder.toString();
    }

    public static Connection getConnection() throws SQLException {
        showConnectionStatistics();
        return connectionPool.getConnection();
    }

    public static void showConnectionStatistics() {
        logger.info("Active connection count: " + connectionPool.getNumActive());
        logger.info("Remain connection count: " + connectionPool.getNumIdle());
    }

    public static void close() throws SQLException {
        connectionPool.close();
    }
}
