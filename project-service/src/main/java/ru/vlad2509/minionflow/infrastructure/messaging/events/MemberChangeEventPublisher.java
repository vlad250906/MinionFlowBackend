package ru.vlad2509.minionflow.infrastructure.messaging.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.application.dto.ProjectMemberChange;
import ru.vlad2509.minionflow.infrastructure.messaging.EventPublisher;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.ConnectionManager;

@ApplicationScoped
public class MemberChangeEventPublisher extends EventPublisher<ProjectMemberChange> {

    @Inject
    ConnectionManager connectionManager;

    private static final String QUEUE = "member-change";

    public MemberChangeEventPublisher() {
        super(QUEUE);
    }

    @Override
    protected Channel setupChannel() {
        Channel channel = connectionManager.requestChannel();
        rabbitService.initQueue(channel, QUEUE, true);
        rabbitService.enableConfirmListener(channel, outboxService::ack, outboxService::nack);
        return channel;
    }

    @Override
    protected String serializeMessage(ProjectMemberChange message) {
        try {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return objectWriter.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
