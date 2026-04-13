package ru.vlad2509.minionflow.infrastructure.persistence.repository.messaging;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.infrastructure.persistence.model.messaging.MessageStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.model.messaging.OutboxMessage;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OutboxRepository implements PanacheRepository<OutboxMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(OutboxRepository.class);

    @Transactional
    public Optional<OutboxMessage> findByMessageId(String messageId) {
        return find("messageId = ?1", messageId).singleResultOptional();
    }

    @Transactional
    public List<OutboxMessage> leaseBatch(int batchSize, int instanceId, int leasePeriod) {
        Instant now = Instant.now();

        List<OutboxMessage> messages = this.find("status = ?1 and (leasedBy < 0 or (leasedUntil is not null and leasedUntil < ?2)) and (nextAttemptAt is null or nextAttemptAt < ?2)",
                        MessageStatus.PENDING, now)
                .page(Page.ofSize(batchSize)).list();

        List<Long> ids = messages.stream().map(msg -> msg.id).toList();
        this.update("leasedBy = ?1, leasedUntil = ?2 where id in ?3", instanceId, now.plusSeconds(leasePeriod), ids);

        return messages;
    }

    @Transactional
    public void markSent(String messageId) {
        int cnt = this.update("status = ?1, leasedBy = -1, leasedUntil = null where messageId = ?2", MessageStatus.DONE, messageId);
        if(cnt <= 0)
            LOG.error("markSent failed, messageId: {}", messageId);
    }

    @Transactional
    public void markFailed(String messageId, String reason) {
        int cnt = this.update("status = ?1, failReason = ?2, leasedBy = -1, leasedUntil = null where messageId = ?3", MessageStatus.FAILED, reason, messageId);
        if(cnt <= 0)
            LOG.error("markFailed failed, messageId: {}", messageId);
    }

    @Transactional
    public void markRetry(String messageId, int retryIn) {
        Instant now = Instant.now();
        int cnt = this.update("attemptsRemaining = attemptsRemaining - 1, nextAttemptAt = ?1, leasedBy = -1, leasedUntil = null where messageId = ?2", now.plusSeconds(retryIn), messageId);
        if(cnt <= 0)
            LOG.error("markRetry failed, messageId: {}", messageId);
    }

}
