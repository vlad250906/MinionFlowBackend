package ru.vlad2509.minionflow.application.util;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.infrastructure.persistence.model.EmailMessageEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.EmailMessageRepository;

import java.util.List;

@ApplicationScoped
public class EmailService {

    @Inject
    EmailMessageRepository emailMessageRepository;

    @ConfigProperty(name = "identity-service.email_batch", defaultValue = "10")
    int batchSize;

    @ConfigProperty(name = "identity-service.instance-id", defaultValue = "1")
    int instanceId;

    @ConfigProperty(name = "identity-service.take-email-limit", defaultValue = "300")
    int takeEmailLimit;

    @ConfigProperty(name = "identity-service.email-max-attempts", defaultValue = "5")
    int emailMaxAttempts;

    @Transactional
    public void scheduleSending(String email, String content){
        EmailMessageEntity entity = new EmailMessageEntity(email, content, emailMaxAttempts);
        emailMessageRepository.persist(entity);
    }

    @Scheduled(every="5s")
    public void sendEmailBatch() {
        List<EmailMessageEntity> batch = emailMessageRepository.takeMessagePage(batchSize, instanceId, takeEmailLimit);

        for (EmailMessageEntity message : batch) {
            SendingResult result = sendEmail(message.email, message.content);
            switch (result) {
                case SUCCESS -> emailMessageRepository.markSent(message.id);
                case IMPOSSIBLE -> emailMessageRepository.markFail(message.id);
                case UNAVAILABLE -> emailMessageRepository.markTryAgain(message.id, calcDelay(message));
            }
        }

        emailMessageRepository.releaseMessagePage(batch);

    }

    private SendingResult sendEmail(String email, String message) {
        return SendingResult.IMPOSSIBLE;
    }

    private int calcDelay(EmailMessageEntity entity) {
        return (int) Math.max(2, Math.min(300, Math.pow(2, (emailMaxAttempts - entity.attempts))));
    }

    enum SendingResult {
        SUCCESS, UNAVAILABLE, IMPOSSIBLE;
    }

}
