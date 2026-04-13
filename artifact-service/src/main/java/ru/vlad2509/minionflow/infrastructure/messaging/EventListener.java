package ru.vlad2509.minionflow.infrastructure.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.RabbitPool;
import ru.vlad2509.minionflow.infrastructure.messaging.rabbit.RabbitService;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.messaging.InboxRepository;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public abstract class EventListener<T> {

    @Inject
    InboxRepository inboxRepository;

    @Inject
    RabbitPool rabbitPool;

    @Inject
    RabbitService rabbitService;

    private final Consumer<T> eventHandler;
    private final boolean inboxCheck;
    private final String queueName;
    private Channel channel;
    private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);

    public void startListening(){
        channel = setupChannel();
        rabbitService.registerConsumer(channel, queueName, (tag, del) -> rabbitPool.execute(() -> asyncProcess(tag, del)));
    }

    protected EventListener(String queueName, boolean inboxCheck, Consumer<T> eventHandler) {
        this.queueName = queueName;
        this.inboxCheck = inboxCheck;
        this.eventHandler = eventHandler;
    }

    protected boolean isUniqueMessage(String messageId) {
        return inboxRepository.createIfNotExist(messageId);
    }

    private void asyncProcess(String consumerTag, Delivery delivery) {
        String messageId = delivery.getProperties().getMessageId();
        String payload = new String(delivery.getBody(), StandardCharsets.UTF_8);
        long tag = delivery.getEnvelope().getDeliveryTag();
        T message = null;

        try {
            if (inboxCheck && !isUniqueMessage(messageId))
                return;
        } catch (Exception e) {
            rabbitService.nackConsumed(channel, tag, false);
            return;
        }

        try {
            message = parse(payload);
        } catch (Exception ex) {
            LOG.warn("Error parsing message from RabbitMQ", ex);
            rabbitService.nackConsumed(channel, tag, true);
            inboxRepository.markFailed(messageId, "[PARSE] Malformed message");
            return;
        }


        try {
            eventHandler.accept(message);
        } catch (Exception ex) {
            LOG.warn("Error executing message handler for RabbitMQ queue", ex);
            rabbitService.nackConsumed(channel, tag, false);
            inboxRepository.markFailed(messageId, "[EXEC] Error executing message handler");
        }

        rabbitService.ackConsumed(channel, tag);
        inboxRepository.markDone(messageId);

    }

    protected abstract Channel setupChannel();

    protected abstract T parse(String payload);

}
