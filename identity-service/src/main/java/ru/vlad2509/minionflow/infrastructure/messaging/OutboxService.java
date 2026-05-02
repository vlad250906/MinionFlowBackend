package ru.vlad2509.minionflow.infrastructure.messaging;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.infrastructure.persistence.model.messaging.OutboxMessage;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.messaging.OutboxRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class OutboxService {

    @Inject
    OutboxRepository outboxRepository;

    @ConfigProperty(name = "service-common.message_batch", defaultValue = "10")
    int batchSize;

    @ConfigProperty(name = "service-common.instance-id", defaultValue = "1")
    int instanceId;

    @ConfigProperty(name = "service-common.lease_timeout", defaultValue = "300")
    int leaseTimeout;

    @ConfigProperty(name = "service-common.max-attempts", defaultValue = "5")
    int maxAttempts;

    private final ConcurrentMap<String, EventPublisher<?>> senders = new ConcurrentHashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(OutboxService.class);

    void startup(@Observes StartupEvent event) {}

    @Transactional
    public void add(String messageId, String queue, String content) {
        OutboxMessage outboxMessage = new OutboxMessage(messageId, queue, content, maxAttempts);
        outboxRepository.persist(outboxMessage);
    }

    public void registerSender(String queue, EventPublisher<?> sender) {
        senders.put(queue, sender);
    }

    @Scheduled(every = "5s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void processOutbox() {
        List<OutboxMessage> outboxMessages = outboxRepository.leaseBatch(batchSize, instanceId, leaseTimeout);
        for (OutboxMessage outboxMessage : outboxMessages) {
            EventPublisher<?> publisher = senders.getOrDefault(outboxMessage.queue, null);
            if (publisher == null) {
                LOG.error("No EventPublisher found for queue {}", outboxMessage.queue);
                outboxRepository.markFailed(outboxMessage.messageId, "[BUG] No EventPublisher found");
                continue;
            }

            if (!publisher.sendToBroker(outboxMessage.messageId, outboxMessage.content))
                nack(outboxMessage.messageId, "[FAIL] channel publish failed with exception");
        }
    }

    @Transactional
    public void ack(String messageId) {
        outboxRepository.markSent(messageId);
    }

    @Transactional
    public void nack(String messageId, String reason) {
        OutboxMessage message = outboxRepository.findByMessageId(messageId).orElseThrow(() -> new RuntimeException("can't nack, because message wasn't found in outbox"));
        if (message.attemptsRemaining <= 1) {
            outboxRepository.markFailed(messageId, reason);
            return;
        }
        int attemptTimeout = calcAttemptTimeout(message.attemptsRemaining);
        outboxRepository.markRetry(messageId, attemptTimeout);
    }

    private int calcAttemptTimeout(int attemptsRemaining) {
        return (int) Math.max(2, Math.min(300, Math.pow(2, (maxAttempts - attemptsRemaining + 1))));
    }

}
