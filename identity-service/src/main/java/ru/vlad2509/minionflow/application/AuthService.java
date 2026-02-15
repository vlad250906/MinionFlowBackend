package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import ru.vlad2509.minionflow.application.dto.TokenPair;
import ru.vlad2509.minionflow.application.dto.UserInfo;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.PasswordService;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;
import ru.vlad2509.minionflow.infrastructure.persistence.model.SessionEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.AccountStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.UserRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class AuthService {

    @Inject
    TokenService tokenService;

    @Inject
    SessionService sessionService;

    @Inject
    PasswordService passwordService;

    @Inject
    UserRepository userRepository;

    private final Set<String> groups = new HashSet<>();

    public AuthService() {
        groups.add("USER");
    }

    @Transactional
    public TokenPair login(EmailVo email, UsernameVo username, @NotEmpty String password) {
        UserEntity user = ((email != null) ?
                userRepository.findByEmailOptional(email) :
                userRepository.findByUsernameOptional(username))
                .orElseThrow(() -> new ApiException(ApiError.INVALID_CREDENTIALS, "user not found"));
        UserInfo userInfo = new UserInfo(user.userId, user.email, user.username, user.status);

        if (!passwordService.verifyPassword(password, user.passwordHash))
            throw new ApiException(ApiError.INVALID_CREDENTIALS, "password incorrect");

        if (user.status == AccountStatus.CREATED)
            throw new ApiException(ApiError.EMAIL_NOT_VERIFIED);

        if (user.status == AccountStatus.SUSPENDED)
            throw new ApiException(ApiError.ACCOUNT_SUSPENDED);

        Instant issueTime = Instant.now();
        SessionEntity sessionEntity = sessionService.persistSession(user);

        String accessJwt = tokenService.createAccessToken(userInfo, groups, issueTime);
        String refreshJwt = tokenService.creteRefreshToken(userInfo, sessionEntity.sessionId, sessionEntity.jwtId, issueTime);

        return new TokenPair(user.userId, accessJwt, refreshJwt, issueTime);
    }

    @Transactional
    public TokenPair refreshToken(String refreshToken) {
        SessionEntity session = sessionService.getSession(refreshToken);
        UserEntity user = session.user;
        UserInfo userInfo = new UserInfo(user.userId, user.email, user.username, user.status);

        UUID newJwtId = UUID.randomUUID();
        Instant issueTime = Instant.now();
        session.jwtId = newJwtId;

        String accessJwt = tokenService.createAccessToken(userInfo, groups, issueTime);
        String refreshJwt = tokenService.creteRefreshToken(userInfo, session.sessionId, newJwtId,
                issueTime);
        return new TokenPair(user.userId, accessJwt, refreshJwt, issueTime);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (!sessionService.logout(sessionService.getSession(refreshToken)))
            throw new ApiException(ApiError.UNAUTHORIZED, "session not found");
    }

    @Transactional
    public void logoutAll(String refreshToken) {
        if (!sessionService.logoutAll(sessionService.getSession(refreshToken).user.userId))
            throw new ApiException(ApiError.UNAUTHORIZED, "session not found");
    }

}
