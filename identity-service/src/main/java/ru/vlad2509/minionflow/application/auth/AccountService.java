package ru.vlad2509.minionflow.application.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.PasswordService;
import ru.vlad2509.minionflow.infrastructure.persistence.model.VerificationTicketEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.AccountStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.VerificationTicketType;
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

        return user.userId;
    }

    public boolean isVerificationRequired(UUID userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findByIdOptional(userId);
        if (userEntityOptional.isEmpty())
            throw new ApiException(ApiError.USER_NOT_FOUND_ID, "user with id = " + userId + " not found");
        UserEntity user = userEntityOptional.get();

        return user.status == AccountStatus.CREATED;
    }

    @Transactional
    public void verifyRegistration(UUID accountId, UUID verificationToken) {
        Optional<UserEntity> userOptional = userRepository.findByIdOptional(accountId);
        if (userOptional.isEmpty())
            throw new ApiException(ApiError.EMAIL_NOT_VERIFIED, "user not found");
        if (userOptional.get().status != AccountStatus.CREATED)
            throw new ApiException(ApiError.EMAIL_ALREADY_VERIFIED);

        Optional<VerificationTicketEntity> ticketOptional = verificationTicketRepository.findByUserAndType(accountId,
                VerificationTicketType.REGISTER_TICKET);
        if (ticketOptional.isEmpty())
            throw new ApiException(ApiError.UNEXPECTED_ERROR, "verificationticket not found");
        if (!ticketOptional.get().verificationToken.equals(verificationToken))
            throw new ApiException(ApiError.EMAIL_NOT_VERIFIED, "wrong verification token");

        userOptional.get().status = AccountStatus.ACTIVE;
        verificationTicketRepository.delete(ticketOptional.get());
    }

}
