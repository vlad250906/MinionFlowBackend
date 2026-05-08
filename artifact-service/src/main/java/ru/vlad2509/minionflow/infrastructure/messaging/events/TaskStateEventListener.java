package ru.vlad2509.minionflow.infrastructure.messaging.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rabbitmq.client.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.application.dto.engine.BaseTaskState;
import ru.vlad2509.minionflow.application.dto.messaging.ProjectMemberChange;
import ru.vlad2509.minionflow.infrastructure.messaging.EventListener;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;

@ApplicationScoped
public class TaskStateEventListener extends EventListener<BaseTaskState> {

    @Inject
    ConnectionManager connectionManager;

    private static final String QUEUE = "TASK_STATE_PATCHES";

    protected TaskStateEventListener() {
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
    protected BaseTaskState parse(String payload) {
        try {
            ObjectReader objectReader = new ObjectMapper().readerFor(BaseTaskState.class);
            return objectReader.readValue(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
