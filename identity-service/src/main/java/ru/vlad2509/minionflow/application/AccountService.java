package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.application.dto.UserInfo;
import ru.vlad2509.minionflow.application.dto.messaging.UserChange;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.PasswordService;
import ru.vlad2509.minionflow.domain.User;
import ru.vlad2509.minionflow.domain.UserSession;
import ru.vlad2509.minionflow.domain.VerificationTicket;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;
import ru.vlad2509.minionflow.infrastructure.messaging.events.UserChangeEventPublisher;
import ru.vlad2509.minionflow.domain.enums.AccountStatus;
import ru.vlad2509.minionflow.domain.enums.VerificationTicketType;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.UserRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.VerificationTicketRepository;

import java.util.UUID;

@ApplicationScoped
public class AccountService {

    @ConfigProperty(name = "identity-service.registration-token-ttl", defaultValue = "600")
    int registrationTokenTtl;

    @Inject
    UserRepository userRepository;

    @Inject
    VerificationTicketRepository verificationTicketRepository;

    @Inject
    PasswordService passwordService;

    @Inject
    EmailTemplateService emailTemplateService;

    @Inject
    SessionService sessionService;

    @Inject
    UserChangeEventPublisher userChangeEventPublisher;

    @Transactional
    public UUID register(EmailVo email, UsernameVo username, String password) {
        String passwordHash = passwordService.hashNew(password);
        User user = new User(email.value(), username.value(), passwordHash);

        if (userRepository.findByEmailOptional(email).isPresent())
            throw new ApiException(ApiError.EMAIL_TAKEN);

        if (userRepository.findByUsernameOptional(username).isPresent())
            throw new ApiException(ApiError.USERNAME_TAKEN);

        userRepository.create(user);

        VerificationTicket ticket = new VerificationTicket(user,
                VerificationTicketType.REGISTER_TICKET, UUID.randomUUID(), registrationTokenTtl);
        verificationTicketRepository.create(ticket);
        emailTemplateService.registration(email.value(), ticket);
        userChangeEventPublisher.publish(new UserChange(user.getId(), user.getUsername()));

        return user.getId();
    }

    public boolean isVerificationRequired(UUID userId) {
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND_ID, "user with id = " + userId + " not found"));
        return user.getStatus() == AccountStatus.CREATED;
    }

    @Transactional
    public void verifyRegistration(UUID accountId, UUID verificationToken) {
        User user = userRepository.findByIdOptional(accountId)
                .orElseThrow(() -> new ApiException(ApiError.EMAIL_NOT_VERIFIED, "user not found"));
        if (user.getStatus() != AccountStatus.CREATED)
            throw new ApiException(ApiError.EMAIL_ALREADY_VERIFIED);

        VerificationTicket ticket = verificationTicketRepository.findByUserAndType(accountId,
                        VerificationTicketType.REGISTER_TICKET)
                .orElseThrow(() -> new ApiException(ApiError.UNEXPECTED_ERROR, "verificationticket not found"));
        if (!ticket.isValid(verificationToken))
            throw new ApiException(ApiError.EMAIL_NOT_VERIFIED, "wrong verification token or expired");

        user.setStatus(AccountStatus.ACTIVE);
        userRepository.updateStatus(user);
        verificationTicketRepository.delete(ticket.getInternalId());
    }

    @Transactional
    public void changePassword(String refreshToken, String oldPassword, String newPassword) {
        UserSession session = sessionService.getSession(refreshToken);
        User user = session.getUser();

        if (!passwordService.verifyPassword(oldPassword, user.getPasswordHash()))
            throw new ApiException(ApiError.INVALID_CREDENTIALS, "old password incorrect");

        user.setPasswordHash(passwordService.hashNew(newPassword));
        userRepository.updatePasswordHash(user);
        sessionService.logout(session);
    }

    @Transactional
    public void changeUsername(String refreshToken, UsernameVo newUsername) {
        if (userRepository.findByUsernameOptional(newUsername).isPresent())
            throw new ApiException(ApiError.USERNAME_TAKEN);

        UserSession session = sessionService.getSession(refreshToken);
        User user = session.getUser();
        user.setUsername(newUsername);
        userRepository.updateUsername(user);

        userChangeEventPublisher.publish(new UserChange(user.getId(), user.getUsername()));
    }

    public UserInfo getUserInfo(UUID userId) {
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new ApiException(ApiError.UNAUTHORIZED, "user not found"));
        return new UserInfo(user.getId(), user.getEmail(), user.getUsername(), user.getStatus());
    }

}
