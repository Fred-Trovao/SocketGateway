package com.taliyev.socketgateway.util;

public class Client {
    private String host;
    private Integer port;
    private String description;
    private String welcomeMessage;

    public Client() {
    }

    public Client(String host, Integer port) {
        this(host, port, null);
    }

    public Client(String host, String port) {
        this(host, CastingOperations.parseInt(port), null);
    }

    public Client(String host, String port, String description) {
        this(host, CastingOperations.parseInt(port), description);
    }

    public Client(String host, Integer port, String description) {
        this.host = host;
        this.port = port;
        this.description = description;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Client{");
        sb.append("host='").append(host).append('\'');
        sb.append(", port=").append(port);
        sb.append(", description=").append(description);
        sb.append(", welcomeMessage=").append(welcomeMessage);
        sb.append('}');
        return sb.toString();
    }
}
