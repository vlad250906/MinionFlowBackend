package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.PasswordService;
import ru.vlad2509.minionflow.domain.User;
import ru.vlad2509.minionflow.domain.VerificationTicket;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.enums.VerificationTicketType;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.UserRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.VerificationTicketRepository;

import java.util.UUID;

@ApplicationScoped
public class RecoveryService {

    @ConfigProperty(name = "identity-service.recovery-token-ttl", defaultValue = "300")
    int registrationTokenTtl;

    @Inject
    UserRepository userRepository;

    @Inject
    EmailTemplateService emailTemplateService;

    @Inject
    VerificationTicketRepository verificationTicketRepository;

    @Inject
    PasswordService passwordService;

    @Transactional
    public void beginRecovery(EmailVo email) {
        User user = userRepository.findByEmailOptional(email).orElse(null);
        if (user == null)
            return;

        verificationTicketRepository.findByUserAndType(user.getId(), VerificationTicketType.RECOVERY_TICKET)
                .ifPresent(verificationTicket ->
                        verificationTicketRepository.delete(verificationTicket.getInternalId()));

        VerificationTicket ticket = new VerificationTicket(user,
                VerificationTicketType.RECOVERY_TICKET, UUID.randomUUID(), registrationTokenTtl);
        verificationTicketRepository.create(ticket);

        emailTemplateService.recovery(user.getEmail(), ticket);
    }

    @Transactional
    public void endRecovery(UUID userId, UUID verificationToken, String newPassword) {
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new ApiException(ApiError.RECOVERY_FAILED, "user not found"));

        VerificationTicket ticket = verificationTicketRepository.findByUserAndType(userId, VerificationTicketType.RECOVERY_TICKET)
                .orElseThrow(() -> new ApiException(ApiError.RECOVERY_FAILED, "ticket not found"));

        if (!ticket.isValid(verificationToken))
            throw new ApiException(ApiError.RECOVERY_FAILED, "wrong verification token or expired");

        user.setPasswordHash(passwordService.hashNew(newPassword));
        userRepository.updatePasswordHash(user);
        verificationTicketRepository.delete(ticket.getInternalId());
    }

}
