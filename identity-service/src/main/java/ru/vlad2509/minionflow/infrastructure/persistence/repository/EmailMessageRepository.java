package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.infrastructure.persistence.model.EmailMessageEntity;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class EmailMessageRepository implements PanacheRepository<EmailMessageEntity> {

    @Inject
    EntityManager em;

    @Transactional
    @SuppressWarnings("unchecked")
    public List<EmailMessageEntity> leaseMessagePage(int pageSize, int instanceId, int takeDurationSeconds) {
        return em.createNativeQuery("""
                with ids as (
                    select id
                    from email_messages
                    where is_sent = false and is_failed = false 
                        and (taken_by = -1 or taken_until < :now)
                        and next_try_in < :now
                    order by created_at desc
                    limit :limit
                    for update skip locked 
                )
                update email_messages msg
                set taken_by = :instanceId, taken_until = :takenUntil
                from ids
                where msg.id = ids.id
                returning msg.*
            """, EmailMessageEntity.class)
                .setParameter("now", Instant.now())
                .setParameter("limit", pageSize)
                .setParameter("instanceId", instanceId)
                .setParameter("takenUntil", Instant.now().plusSeconds(takeDurationSeconds))
                .getResultList();
    }

    @Transactional
    public void releaseMessage(EmailMessageEntity emailMessageEntity) {
        update("takenBy = -1 where id in ?1", emailMessageEntity.id);
    }

    @Transactional
    public void markFail(long id, String reason) {
        update("isFailed = true, failReason = ?1, attempts = 0 where id = ?2", reason, id);
    }

    @Transactional
    public void markTryAgain(long id, int delay) {
        update("attempts = attempts - 1, nextTryIn = ?1 where id = ?2",
                Instant.now().plusSeconds(delay), id);
    }

    @Transactional
    public void markSent(long id) {
        update("isSent = true where id = ?1", id);
    }


}
