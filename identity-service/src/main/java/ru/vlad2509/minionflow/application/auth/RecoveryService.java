package ru.vlad2509.minionflow.application.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.EmailService;
import ru.vlad2509.minionflow.application.util.PasswordService;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.VerificationTicketEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.VerificationTicketType;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.UserRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.VerificationTicketRepository;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RecoveryService {

    @Inject
    UserRepository userRepository;

    @Inject
    EmailService emailService;

    @Inject
    VerificationTicketRepository verificationTicketRepository;

    @Inject
    PasswordService passwordService;

    @Transactional
    public void beginRecovery(EmailVo email) {
        UserEntity user = userRepository.findByEmailOptional(email).orElse(null);
        if (user == null)
            return;

        verificationTicketRepository.findByUserAndType(user.userId, VerificationTicketType.RECOVERY_TICKET)
                .ifPresent(verificationTicketEntity ->
                        verificationTicketRepository.delete(verificationTicketEntity));

        VerificationTicketEntity ticket = new VerificationTicketEntity(user.userId,
                VerificationTicketType.RECOVERY_TICKET, UUID.randomUUID());
        verificationTicketRepository.persist(ticket);

        emailService.scheduleSending(email, "recovery \naccountId=" + user.userId +
                "\nverificationToken=" + ticket.verificationToken);
    }

    @Transactional
    public void endRecovery(UUID userId, UUID verificationToken, String newPassword) {
        UserEntity user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new ApiException(ApiError.RECOVERY_FAILED, "user not found"));

        VerificationTicketEntity ticket = verificationTicketRepository.findByUserAndType(userId, VerificationTicketType.RECOVERY_TICKET)
                .orElseThrow(() -> new ApiException(ApiError.RECOVERY_FAILED, "ticket not found"));

        if (!ticket.verificationToken.equals(verificationToken))
            throw new ApiException(ApiError.RECOVERY_FAILED, "wrong verification token");

        user.passwordHash = passwordService.hashNew(newPassword);
        verificationTicketRepository.delete(ticket);
    }

}
