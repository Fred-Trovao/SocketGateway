package com.taliyev.socketgateway.util;

public enum ConstantsEnum {

    SERVER_PORT("server.port"),
    MAX_ACCEPTED_CONNECTIONS("server.maxAcceptedConnection"),
    CONNECTION_TIMEOUT("server.connectionTimeout"),
    CLIENT_HOST("clients.client.host"),
    CLIENT_PORT("clients.client.port"),
    CLIENT_DESC("clients.client.description"),
    CLIENT_WELCOME_MESSAGE("clients.client.welcomeMessage"),
    DB_CONFIGURATUION("server.dbParams"),
    DB_TYPE("server.dbParams.dbType"),
    DB_USERNAME("server.dbParams.dbUsername"),
    DB_PASSWORD("server.dbParams.dbPassword"),
    DB_HOST("server.dbParams.dbHost"),
    DB_PORT("server.dbParams.dbPort"),
    DB_NAME("server.dbParams.dbName"),
    EXECUTOR_PARAM_INSERT_REQUEST("insert_request"),
    EXECUTOR_PARAM_INSERT_RESPONSE("insert_response");

    private String value;

    ConstantsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public ConstantsEnum getKey(String value) throws Exception {
        for (ConstantsEnum constant: ConstantsEnum.values()) {
            if (value != null && constant.getValue() != null && value.equals(constant.getValue())) {
                return constant;
            }
        }

        throw new Exception("Constant not found");
    }

}
