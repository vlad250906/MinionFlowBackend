package ru.vlad2509.minionflow.infrastructure.messaging.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ApplicationScoped
public class ConnectionManager {

    @Inject
    RabbitMQClient rabbitMQClient;

    private static Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private volatile Connection connection;

    public synchronized Channel requestChannel() {
        try {
            if (connection == null || !connection.isOpen())
                connection = rabbitMQClient.connect();
            return connection.createChannel();
        } catch (IOException e) {
            LOG.error("Failed to open new RabbitMQ channel", e);
            return null;
        }
    }

    public void releaseChannel(Channel channel) {
        try {
            channel.close();
        } catch (Exception e) {
            LOG.error("Failed to close RabbitMQ channel", e);
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        if(connection != null && connection.isOpen())
            connection.close();
    }

}
