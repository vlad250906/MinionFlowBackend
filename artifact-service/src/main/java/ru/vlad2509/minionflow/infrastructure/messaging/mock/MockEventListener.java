package ru.vlad2509.minionflow.infrastructure.messaging.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.infrastructure.messaging.EventListener;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.RabbitService;

import java.io.IOException;
import java.util.Random;

@ApplicationScoped
public class MockEventListener extends EventListener<MockMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(MockEventListener.class);

    @Inject
    ConnectionManager connectionManager;

    private static final String QUEUE = "test-queue";
    @Inject
    RabbitService rabbitService;

    public MockEventListener() {
        super(QUEUE, true, MockEventListener::eventHandler);
    }

    void startup(@Observes StartupEvent event) {
        startListening();
    }

    public static void eventHandler(MockMessage message) {
        Random random = new Random();
        if (random.nextInt(10) < 7)
            throw new RuntimeException("unluck :(");
        LOG.info("Received: {}; {}", message.text(), message.num());
    }

    @Override
    protected Channel setupChannel() {
        Channel channel = connectionManager.requestChannel();

        rabbitService.enableConfirmSending(channel);

        try {
            channel.exchangeDeclare("global", BuiltinExchangeType.TOPIC, true);
            channel.queueDeclare(QUEUE, true, false, false, null);
            channel.queueBind(QUEUE, "global", QUEUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return channel;
    }

    @Override
    protected MockMessage parse(String payload) {
        try {
            ObjectReader reader = new ObjectMapper().readerFor(MockMessage.class);
            return reader.readValue(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
