package models;

public class RabbitMqConfig {
    private final String rabbitMqAddress;
    private final String rabbitMqPort;
    private final String rabbitMqUserName;
    private final String rabbitMqPassword;
    private final String virtualHost;

    public RabbitMqConfig(String rabbitMqAddress, String rabbitMqPort, String rabbitMqUserName, String rabbitMqPassword, String virtualHost) {
        this.rabbitMqAddress = rabbitMqAddress;
        this.rabbitMqPort = rabbitMqPort;
        this.rabbitMqUserName = rabbitMqUserName;
        this.rabbitMqPassword = rabbitMqPassword;
        this.virtualHost = virtualHost;
    }

    public String getRabbitMqAddress() {
        return rabbitMqAddress;
    }

    public String getRabbitMqPort() {
        return rabbitMqPort;
    }

    public String getRabbitMqUserName() {
        return rabbitMqUserName;
    }

    public String getRabbitMqPassword() {
        return rabbitMqPassword;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

}