package ru.vlad2509.minionflow.application.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.openapi.annotations.links.Link;
import ru.vlad2509.minionflow.application.dto.DecodedRefreshToken;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.EmailService;
import ru.vlad2509.minionflow.application.util.PasswordService;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.infrastructure.persistence.model.SessionEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.VerificationTicketEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.AccountStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.VerificationTicketType;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.SessionRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.UserRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.VerificationTicketRepository;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AccountService {

    @Inject
    UserRepository userRepository;

    @Inject
    VerificationTicketRepository verificationTicketRepository;

    @Inject
    PasswordService passwordService;

    @Inject
    EmailService emailService;

    @Inject
    TokenService tokenService;

    @Inject
    SessionService sessionService;

    @Transactional
    public UUID register(String email, String username, String password) {
        if (userRepository.findByEmailOptional(email).isPresent())
            throw new ApiException(ApiError.EMAIL_TAKEN);
        if (userRepository.findByUsernameOptional(username).isPresent())
            throw new ApiException(ApiError.USERNAME_TAKEN);

        String passwordHash = passwordService.hashNew(password);
        UserEntity user = new UserEntity(email, username, passwordHash);
        userRepository.persist(user);

        VerificationTicketEntity ticket = new VerificationTicketEntity(user.userId,
                VerificationTicketType.REGISTER_TICKET, UUID.randomUUID());
        verificationTicketRepository.persist(ticket);

        emailService.scheduleSending(email, "registration \naccountId=" + user.userId +
                "\nverificationToken=" + ticket.verificationToken);

        return user.userId;
    }

    public boolean isVerificationRequired(UUID userId) {
        UserEntity user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND_ID, "user with id = " + userId + " not found"));
        return user.status == AccountStatus.CREATED;
    }

    @Transactional
    public void verifyRegistration(UUID accountId, UUID verificationToken) {
        UserEntity user = userRepository.findByIdOptional(accountId)
                .orElseThrow(() -> new ApiException(ApiError.EMAIL_NOT_VERIFIED, "user not found"));
        if (user.status != AccountStatus.CREATED)
            throw new ApiException(ApiError.EMAIL_ALREADY_VERIFIED);

        VerificationTicketEntity ticket = verificationTicketRepository.findByUserAndType(accountId,
                        VerificationTicketType.REGISTER_TICKET)
                .orElseThrow(() -> new ApiException(ApiError.UNEXPECTED_ERROR, "verificationticket not found"));
        if (!ticket.verificationToken.equals(verificationToken))
            throw new ApiException(ApiError.EMAIL_NOT_VERIFIED, "wrong verification token");

        user.status = AccountStatus.ACTIVE;
        verificationTicketRepository.delete(ticket);
    }

    @Transactional
    public void changePassword(String refreshToken, String oldPassword, String newPassword) {
        System.out.println(refreshToken);
        SessionEntity session = sessionService.getSession(refreshToken);
        UserEntity userEntity = session.user;

        if (!passwordService.verifyPassword(oldPassword, userEntity.passwordHash))
            throw new ApiException(ApiError.INVALID_CREDENTIALS, "old password incorrect");

        userEntity.passwordHash = passwordService.hashNew(newPassword);
        sessionService.logout(session);
    }

    @Transactional
    public void changeUsername(String refreshToken, String newUsername) {
        if (userRepository.findByUsernameOptional(newUsername).isPresent())
            throw new ApiException(ApiError.USERNAME_TAKEN);

        SessionEntity session = sessionService.getSession(refreshToken);
        UserEntity userEntity = session.user;
        userEntity.username = newUsername;
    }

}
