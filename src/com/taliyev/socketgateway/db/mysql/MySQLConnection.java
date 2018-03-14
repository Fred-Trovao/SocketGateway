package com.taliyev.socketgateway.db.mysql;

import com.taliyev.socketgateway.util.CastingOperations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Date;

/*
 * Author: toghrul
 * Date: 11/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public class MySQLConnection {

    private Logger logger;

    public MySQLConnection() {
        DBConnection.initialize();
        logger = LogManager.getLogger(MySQLConnection.class);
    }

    // Register request & return request id
    public Integer insert(String request, Date startTime, Date finishTime, String response, String status) throws SQLException {
        Integer id = getNextRequestId();
        insert(id, request, startTime, finishTime, response, status);
        return id;
    }

    // Execute Sql
    public void insert(Integer id, String request, Date startTime, Date finishTime, String response, String status) throws SQLException {
        logger.debug("Request received for registering in DB. {" +
                    "id:"+ id +
                    ", request:" + request +
                    ", startTime:" + startTime +
                    ", finishTime" + finishTime +
                    ", response:" + response +
                    ", status:" + status + "}");
        String sql = "insert into cai_requests(id, request, start_time, finish_time, returned_response, response_status) " +
                "values (?, ?, ?, ?, ?, ?)";
        Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        int i = 1;
        if (id != null) {
            preparedStatement.setInt(i++, id);
        } else {
            preparedStatement.setNull(i++, Types.INTEGER);
        }
        preparedStatement.setString(i++, request);
        preparedStatement.setString(i++, CastingOperations.convertToMySQLDate(startTime));
        preparedStatement.setString(i++, CastingOperations.convertToMySQLDate(finishTime));
        preparedStatement.setString(i++, response);
        preparedStatement.setString(i++, status);

        preparedStatement.execute();

        close(connection, null, preparedStatement, null);

        logger.debug("Request registered successfully in DB... ");
    }

    // Get ID for request
    public Integer getNextRequestId() throws SQLException {
        String sql = "select coalesce(max(id), 0) + 1 from cai_requests";
        Connection connection = DBConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        Integer id = resultSet.next() ? resultSet.getInt(1) : null;
        close(connection, statement, null, resultSet);
        return id;
    }

    // Register response
    public void registerResponse(String nodeType, Integer id, Integer requestId,
                                 String response, Date startTime, Date finishTime) throws SQLException {
        if (!("ema15".equalsIgnoreCase(nodeType) || "ema16".equalsIgnoreCase(nodeType))) {
            logger.error("Node Type is not correct. nodeType=" + nodeType);
            return;
        }

        logger.debug("Response received for registering in DB. {" +
                "nodeType:" + nodeType +
                ", id:"+ id +
                ", requestId:" + requestId +
                ", startTime:" + startTime +
                ", finishTime" + finishTime +
                ", response:" + response + "}");
        String sql = "insert into " + nodeType + "_responses(id, request_id, resp, start_time, finish_time) " +
                "values (?, ?, ?, ?, ?)";
        Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        int i = 1;
        if (id != null) {
            preparedStatement.setInt(i++, id);
        } else {
            preparedStatement.setNull(i++, Types.INTEGER);
        }
        preparedStatement.setInt(i++, requestId);
        preparedStatement.setString(i++, response);
        preparedStatement.setString(i++, CastingOperations.convertToMySQLDate(startTime));
        preparedStatement.setString(i++, CastingOperations.convertToMySQLDate(finishTime));

        preparedStatement.execute();
        close(connection, null, preparedStatement, null);

        logger.debug("Response registered successfully in DB... ");
    }

    // Close all connections
    public void close(Connection connection, Statement statement, PreparedStatement preparedStatement, ResultSet resultSet) {
        logger.debug("Starting close all DB parameters");
        closeResultSet(resultSet);
        closeStatement(statement);
        closePreparedStatement(preparedStatement);
        closeConnection(connection);
        logger.debug("Finishing closing all DB parameters");
    }

    // Close DB connection
    public void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            logger.error("Can't close DB connection", ex);
        }
    }

    // Close Statement
    public void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ex) {
            logger.error("Can't close DB statement", ex);
        }
    }

    // Close Prepared Statement
    public void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException ex) {
            logger.error("Can't close DB prepared statement", ex);
        }
    }

    // Close statement
    public void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException ex) {
            logger.error("Can't close DB Result Set", ex);
        }
    }
}
