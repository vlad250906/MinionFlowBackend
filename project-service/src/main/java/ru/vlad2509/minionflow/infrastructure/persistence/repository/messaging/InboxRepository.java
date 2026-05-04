package ru.vlad2509.minionflow.infrastructure.persistence.repository.messaging;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.infrastructure.persistence.model.messaging.InboxMessage;
import ru.vlad2509.minionflow.infrastructure.persistence.model.messaging.MessageStatus;

import java.util.Optional;

@ApplicationScoped
public class InboxRepository implements PanacheRepository<InboxMessage> {

    @ConfigProperty(name = "service-common.max-attempts-inbox", defaultValue = "5")
    int maxAttempts;

    @Transactional
    public boolean createIfNotExist(String messageId) {
        Optional<InboxMessage> message = find("messageId = ?1", messageId).singleResultOptional();
        if (message.isEmpty()) {
            this.persist(new InboxMessage(messageId, maxAttempts));
            return true;
        }

        if (message.get().status != MessageStatus.FAILED || message.get().attemptsRemaining <= 0)
            return false;

        message.get().status = MessageStatus.PENDING;
        return true;
    }

    @Transactional
    public void markDone(String messageId) {
        this.update("status = ?1, failReason = null where messageId = ?2", MessageStatus.DONE, messageId);
    }

    @Transactional
    public void markFailed(String messageId, String reason) {
        this.update("status = ?1, failReason = ?2, attemptsRemaining = attemptsRemaining - 1 where messageId = ?3", MessageStatus.FAILED, reason, messageId);
    }

}
