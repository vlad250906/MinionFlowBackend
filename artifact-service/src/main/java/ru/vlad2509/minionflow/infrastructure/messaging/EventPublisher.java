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

    private final String routingKey;
    private Channel channel;

    public EventPublisher(String routingKey) {
        this.routingKey = routingKey;
    }

    void init(@Observes StartupEvent event){
        outboxService.registerSender(routingKey, this);
    }

    public void publish(T message) {
        UUID messageId = UUID.randomUUID();
        String payload = serializeMessage(message);
        outboxService.add(messageId.toString(), this.routingKey, payload);
    }

    public void publish(String routingKey, T message) {
        UUID messageId = UUID.randomUUID();
        String payload = serializeMessage(message);
        outboxService.add(messageId.toString(), routingKey, payload);
    }

    public boolean sendToBroker(String messageId, String content) {
        if (channel == null)
            channel = setupChannel();

        return rabbitService.publish(channel, routingKey, messageId, content);
    }

    protected abstract Channel setupChannel();

    protected abstract String serializeMessage(T message);

}
