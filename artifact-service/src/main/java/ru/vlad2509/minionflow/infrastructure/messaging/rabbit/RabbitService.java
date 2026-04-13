package ru.vlad2509.minionflow.infrastructure.messaging.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ApplicationScoped
public class RabbitService {

    @Inject
    RabbitPool rabbitPool;

    private static final Logger LOG = LoggerFactory.getLogger(RabbitService.class);
    private static final Map<Channel, ConfirmTracker> trackers = new ConcurrentHashMap<Channel, ConfirmTracker>();

    public boolean publish(Channel channel, String queue, String messageId, String payload) {
        try {
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .contentType("application/json")
                    .deliveryMode(2) // persistent, сохранится после перезапуска брокера
                    .messageId(messageId)
                    .build();

            ConfirmTracker tracker = trackers.getOrDefault(channel, null);
            if (tracker != null)
                tracker.addMessage(channel.getNextPublishSeqNo(), messageId);

            channel.basicPublish("global", queue, properties, payload.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            LOG.warn("Error publishing to queue: {}", queue, e);
            return false;
        }
    }

    public void registerConsumer(Channel channel, String queue, DeliverCallback deliverCallback) {
        try {
            channel.basicConsume(queue, false, deliverCallback, (tag) -> {
                LOG.warn("Received CancelCallback from queue: {}", queue);
            });
        } catch (Exception e) {
            LOG.error("Error registering consumer to queue: {}", queue, e);
            throw new RuntimeException(e);
        }
    }

    public void ackConsumed(Channel channel, long tag) {
        try {
            channel.basicAck(tag, false);
        } catch (Exception e) {
            LOG.error("Failed to ack for tag: {}", tag, e);
            throw new RuntimeException(e);
        }
    }

    public void nackConsumed(Channel channel, long tag, boolean isHardFail) {
        try {
            channel.basicNack(tag, false, !isHardFail);
        } catch (Exception e) {
            LOG.error("Failed to nack for tag: {}", tag, e);
            throw new RuntimeException(e);
        }
    }

    public void enableConfirmSending(Channel channel){
        try {
            channel.basicRecover(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enableConfirmListener(Channel channel, Consumer<String> onAck, BiConsumer<String, String> onNack) {
        try {
            channel.confirmSelect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ConfirmTracker tracker = new ConfirmTracker(rabbitPool, onAck, onNack);
        trackers.put(channel, tracker);

        channel.addConfirmListener(
                (seq, multiple) -> tracker.processSeq(seq, multiple, null),
                (seq, multiple) -> tracker.processSeq(seq, multiple, "[NACK] Nack for publish seqNo: " + seq)
        );
        channel.addReturnListener((ret) -> {
            LOG.error("RabbitMQ returned message (probably unroutable), messageId: {}, code: {}, reason: {}",
                    ret.getProperties().getMessageId(), ret.getReplyCode(), ret.getReplyText());
        });
        channel.addShutdownListener((ex) -> tracker.failAll("[SHUT]" + ex.getMessage()));
    }

    public void disableConfirmListener(Channel channel) {
        channel.clearConfirmListeners();
        trackers.remove(channel).failAll("[CLOSE] Channel is about to be closed");
    }

}
