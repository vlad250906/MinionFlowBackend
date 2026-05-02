package ru.vlad2509.minionflow.application.util;

import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.application.port.out.SendingResult;
import ru.vlad2509.minionflow.application.port.out.SmtpService;
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

    @ConfigProperty(name = "service-common.instance-id", defaultValue = "1")
    int instanceId;

    @ConfigProperty(name = "service-common.lease_timeout", defaultValue = "300")
    int takeEmailLimit;

    @ConfigProperty(name = "identity-service.email-max-attempts", defaultValue = "5")
    int emailMaxAttempts;

    private final EmailMessageRepository emailMessageRepository;
    private final SmtpService smtpService;

    private final ExecutorService customExecutor;
    private final AtomicInteger leasedTotal = new AtomicInteger(0);

    @Inject
    public EmailService(@ConfigProperty(name = "identity-service.email-sending-pool-size", defaultValue = "5")
                        int poolSize,
                        EmailMessageRepository emailMessageRepository,
                        SmtpService smtpService) {
        this.emailMessageRepository = emailMessageRepository;
        this.customExecutor = Executors.newFixedThreadPool(poolSize);
        this.smtpService = smtpService;
    }

    @Transactional
    public void scheduleSending(String email, String subject, String content) {
        EmailMessageEntity entity = new EmailMessageEntity(email, subject, content, emailMaxAttempts);
        emailMessageRepository.persist(entity);
    }

    @Scheduled(every = "5s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void sendEmailBatch() {
        if (leasedTotal.get() > 2 * batchSize)
            return;

        List<EmailMessageEntity> batch = emailMessageRepository.leaseMessagePage(batchSize, instanceId, takeEmailLimit);
        leasedTotal.addAndGet(batch.size());

        for (EmailMessageEntity message : batch) {
            CompletableFuture.supplyAsync(() -> smtpService.sendMail(message.email, message.subject, message.content), customExecutor)
                    .thenApply(sendingResult -> afterSent(sendingResult, message.id))
                    .whenComplete((r, e) -> leasedTotal.decrementAndGet());
        }
    }

    @Transactional
    public SendingResult afterSent(SendingResult result, long messageId) {
        EmailMessageEntity message = emailMessageRepository.findById(messageId);
        switch (result) {
            case SUCCESS -> emailMessageRepository.markSent(messageId);
            case IMPOSSIBLE -> emailMessageRepository.markFail(message.id, "hard failure, see logs");
            case UNAVAILABLE -> {
                if (message.attempts - 1 <= 0) {
                    emailMessageRepository.markFail(message.id, "no attempts remain");
                } else {
                    emailMessageRepository.markTryAgain(message.id, calcDelay(message));
                }
            }
        }
        emailMessageRepository.releaseMessage(message);
        return result;
    }

    private int calcDelay(EmailMessageEntity entity) {
        return (int) Math.max(2, Math.min(300, Math.pow(2, (emailMaxAttempts - entity.attempts))));
    }

    @PreDestroy
    private void shutdown() {
        this.customExecutor.shutdown();
    }


}
