package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.infrastructure.persistence.model.EmailMessageEntity;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class EmailMessageRepository implements PanacheRepository<EmailMessageEntity> {

    @Transactional
    public List<EmailMessageEntity> takeMessagePage(int pageSize, int instanceId, int takeDurationSeconds) {
        Instant now = Instant.now();

        List<EmailMessageEntity> candidates = find("isSent = false and isFailed = false and " +
                        "(takenBy = -1 or takenUntil < ?1)" +
                        " and attempts > 0 and nextTryIn < ?1",
                now).page(new Page(pageSize)).list();

        List<Long> ids = candidates.stream().map(cand -> cand.id).toList();

        if(!ids.isEmpty())
            update("takenBy = ?1, takenUntil = ?2 where id in ?3", instanceId, now.plusSeconds(takeDurationSeconds), ids);

        return candidates;
    }

    @Transactional
    public void releaseMessagePage(List<EmailMessageEntity> emailMessageEntities){
        List<Long> ids = emailMessageEntities.stream().map(cand -> cand.id).toList();
        update("takenBy = -1 where id in ?1", ids);
    }

    @Transactional
    public void markFail(long id){
        update("isFailed = true where id = ?1", id);
    }

    @Transactional
    public void markTryAgain(long id, int delay){
        update("attempts = attempts - 1, nextTryIn = ?1 where id = ?2",
                Instant.now().plusSeconds(delay), id);
    }

    @Transactional
    public void markSent(long id){
        update("isSent = true where id = ?1", id);
    }



}
