package ru.vlad2509.minionflow.infrastructure.messaging;

import com.rabbitmq.client.Channel;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.RabbitService;

import java.util.UUID;

public abstract class EventPublisher<T> {

    @Inject
    protected OutboxService outboxService;

    @Inject
    protected RabbitService rabbitService;

    private final String queueName;
    private Channel channel;

    public EventPublisher(String queueName) {
        this.queueName = queueName;
    }

    void init(@Observes StartupEvent event){
        outboxService.registerSender(queueName, this);
    }

    public void publish(T message) {
        UUID messageId = UUID.randomUUID();
        String payload = serializeMessage(message);
        outboxService.add(messageId.toString(), queueName, payload);
    }

    public boolean sendToBroker(String messageId, String content) {
        if (channel == null)
            channel = setupChannel();

        return rabbitService.publish(channel, queueName, messageId, content);
    }

    protected abstract Channel setupChannel();

    protected abstract String serializeMessage(T message);

}
