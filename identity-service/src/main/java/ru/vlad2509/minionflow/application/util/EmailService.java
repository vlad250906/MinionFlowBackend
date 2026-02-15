package ru.vlad2509.minionflow.application.util;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import io.vertx.ext.mail.SMTPException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.infrastructure.persistence.model.EmailMessageEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.EmailMessageRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class EmailService {

    @ConfigProperty(name = "identity-service.email_batch", defaultValue = "10")
    int batchSize;

    @ConfigProperty(name = "identity-service.instance-id", defaultValue = "1")
    int instanceId;

    @ConfigProperty(name = "identity-service.take-email-limit", defaultValue = "300")
    int takeEmailLimit;

    @ConfigProperty(name = "identity-service.email-max-attempts", defaultValue = "5")
    int emailMaxAttempts;

    @ConfigProperty(name = "identity-service.email-sending-pool-size", defaultValue = "5")
    int poolSize;


    @Inject
    EmailMessageRepository emailMessageRepository;

    @Inject
    Mailer mailer;

    private final ExecutorService customExecutor = Executors.newFixedThreadPool(poolSize);

    private final AtomicInteger leasedTotal = new AtomicInteger(0);

    @Transactional
    public void scheduleSending(String email, String content) {
        EmailMessageEntity entity = new EmailMessageEntity(email, content, emailMaxAttempts);
        emailMessageRepository.persist(entity);
    }

    @Scheduled(every = "5s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void sendEmailBatch() {
        if (leasedTotal.get() > 2 * batchSize)
            return;

        List<EmailMessageEntity> batch = emailMessageRepository.leaseMessagePage(batchSize, instanceId, takeEmailLimit);
        leasedTotal.addAndGet(batch.size());

        for (EmailMessageEntity message : batch) {
            CompletableFuture.supplyAsync(() -> sendEmail(message.email, message.content), customExecutor)
                    .thenApply(sendingResult -> afterSent(sendingResult, message.id))
                    .whenComplete((r, e) -> leasedTotal.decrementAndGet());
        }
    }

    @Transactional
    private SendingResult afterSent(SendingResult result, long messageId) {
        EmailMessageEntity message = emailMessageRepository.findById(messageId);
        switch (result) {
            case SUCCESS -> emailMessageRepository.markSent(messageId);
            case IMPOSSIBLE -> emailMessageRepository.markFail(message.id);
            case UNAVAILABLE -> emailMessageRepository.markTryAgain(message.id, calcDelay(message));
        }
        emailMessageRepository.releaseMessage(message);
        return result;
    }

    private SendingResult sendEmail(String email, String message) {
        try {
            mailer.send(Mail.withText(email, "test test test", message));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            SMTPException smtpException = findClause(ex, SMTPException.class);
            if (smtpException == null)
                return SendingResult.UNAVAILABLE;
            if (smtpException.isPermanent())
                return SendingResult.IMPOSSIBLE;
            return SendingResult.UNAVAILABLE;
        }
        return SendingResult.SUCCESS;
    }

    private <T extends Throwable> T findClause(Throwable throwable, Class<T> clazz) {
        Throwable cur = throwable;
        while (cur != null) {
            if (clazz.isInstance(cur))
                return clazz.cast(cur);
            cur = cur.getCause();
        }
        return null;
    }

    private int calcDelay(EmailMessageEntity entity) {
        return (int) Math.max(2, Math.min(300, Math.pow(2, (emailMaxAttempts - entity.attempts))));
    }

    enum SendingResult {
        SUCCESS, UNAVAILABLE, IMPOSSIBLE;
    }

}
