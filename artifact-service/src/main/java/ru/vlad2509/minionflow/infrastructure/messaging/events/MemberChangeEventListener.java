package ru.vlad2509.minionflow.infrastructure.messaging.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rabbitmq.client.Channel;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.infrastructure.messaging.EventListener;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;

@ApplicationScoped
public class MemberChangeEventListener extends EventListener<ProjectMemberChange> {

    @Inject
    ConnectionManager connectionManager;

    private static final String QUEUE = "member-change";

    protected MemberChangeEventListener() {
        super(QUEUE, true);
    }

    @Override
    protected Channel setupChannel() {
        Channel channel = connectionManager.requestChannel();
        rabbitService.initQueue(channel, QUEUE, true);
        rabbitService.enableConfirmSending(channel);
        return channel;
    }

    @Override
    protected ProjectMemberChange parse(String payload) {
        try {
            ObjectReader objectReader = new ObjectMapper().readerFor(ProjectMemberChange.class);
            return objectReader.readValue(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
