package services;

import com.rabbitmq.client.*;
import models.RabbitMqConfig;
import models.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMqService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqService.class);

    private final RabbitMqConfig rabbitMqConfig;
    private final ConnectionFactory connectionFactory;
    private Connection connection;

    // ThreadLocal to ensure each thread has its own Channel
    private final ThreadLocal<Channel> threadLocalChannel = new ThreadLocal<>();

    public RabbitMqService() {
        this.rabbitMqConfig = new RabbitMqConfig(
            "localhost",
            "5672",
            "root",
            "rootpassword",
            "/"
        );

        this.connectionFactory = new ConnectionFactory();
        this.connectionFactory.setUsername(this.rabbitMqConfig.getRabbitMqUserName());
        this.connectionFactory.setPassword(this.rabbitMqConfig.getRabbitMqPassword());

        String virtualHost = this.rabbitMqConfig.getVirtualHost();
        if (virtualHost != null && !virtualHost.equals("")) {
            this.connectionFactory.setVirtualHost(virtualHost);
        }
        this.connectionFactory.setHost(this.rabbitMqConfig.getRabbitMqAddress());
        this.connectionFactory.setPort(Integer.parseInt(this.rabbitMqConfig.getRabbitMqPort()));
    }

    /**
     * Create a new channel, reusing the existing connection.
     * The connection is lazily initialized if it's not already open.
     * Each thread gets its own Channel via ThreadLocal.
     */
    public Channel createNewChannel() throws IOException, TimeoutException {
        if (threadLocalChannel.get() == null) {
            synchronized (this) {
                if (connection == null || !connection.isOpen()) {
                    connection = createConnection();
                }
            }
            // Create a new channel for the current thread
            Channel channel = connection.createChannel();
            threadLocalChannel.set(channel);
        }
        return threadLocalChannel.get();
    }

    /**
     * Create a new RabbitMQ connection.
     */
    private Connection createConnection() throws IOException, TimeoutException {
        logger.info("Creating a new RabbitMQ connection to {}", rabbitMqConfig.getRabbitMqAddress());
        return this.connectionFactory.newConnection();
    }

    /**
     * Close the connection and any open channels.
     * Also closes any thread-local channels.
     */
    public synchronized void close() throws IOException, TimeoutException {
        // Close thread-local channels
        Channel channel = threadLocalChannel.get();
        if (channel != null && channel.isOpen()) {
            channel.close();
            logger.info("Thread-local RabbitMQ channel closed.");
            threadLocalChannel.remove();
        }

        // Close the shared connection
        if (connection != null && connection.isOpen()) {
            connection.close();
            logger.info("RabbitMQ connection closed.");
        }
    }

    public RpcServer newRpcServer(String queueName) throws IOException, TimeoutException {
        Channel channel = this.createNewChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queuePurge(queueName);
        return new RpcServer(channel, queueName);
    }

    public RabbitMqConfig getRabbitMqConfig() {
        return rabbitMqConfig;
    }
}
