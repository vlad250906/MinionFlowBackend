package ru.vlad2509.minionflow.infrastructure.persistence.repository.messaging;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
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

    @Inject
    EntityManager em;

    @Transactional
    public Optional<OutboxMessage> findByMessageId(String messageId) {
        return find("messageId = ?1", messageId).singleResultOptional();
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<OutboxMessage> leaseBatch(int batchSize, int instanceId, int leasePeriod) {
        return em.createNativeQuery("""
                with ids as (
                    select id
                    from outbox_messages
                    where status = 'PENDING'
                        and (leased_by < 0 or (leased_until is not null and leased_until < :now)) 
                        and (next_attempt_at is null or next_attempt_at < :now)
                    order by created_at desc
                    limit :limit
                    for update skip locked 
                )
                update outbox_messages msg
                set leased_by = :instanceId, leased_until = :leasedUntil
                from ids
                where msg.id = ids.id
                returning msg.*
                """, OutboxMessage.class)
                .setParameter("now", Instant.now())
                .setParameter("limit", batchSize)
                .setParameter("instanceId", instanceId)
                .setParameter("leasedUntil", Instant.now().plusSeconds(leasePeriod))
                .getResultList();
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
