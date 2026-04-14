package ru.vlad2509.minionflow.infrastructure.messaging.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rabbitmq.client.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.application.dto.messaging.UserChange;
import ru.vlad2509.minionflow.infrastructure.messaging.EventListener;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;

@ApplicationScoped
public class UserChangeEventListener extends EventListener<UserChange> {

    @Inject
    ConnectionManager connectionManager;

    private static final String QUEUE = "user-change";

    protected UserChangeEventListener() {
        super(QUEUE, true);
    }

    @Override
    protected Channel setupChannel() {
        Channel channel = connectionManager.requestChannel();
        super.rabbitService.enableConfirmSending(channel);
        super.rabbitService.initQueue(channel, QUEUE, true);
        return channel;
    }

    @Override
    protected UserChange parse(String payload) {
        try {
            ObjectReader objectReader = new ObjectMapper().readerFor(UserChange.class);
            return objectReader.readValue(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
