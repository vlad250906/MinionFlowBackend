package ru.vlad2509.minionflow.application.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.application.dto.DecodedRefreshToken;
import ru.vlad2509.minionflow.application.dto.TokenPair;
import ru.vlad2509.minionflow.application.dto.UserInfo;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.PasswordService;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.infrastructure.persistence.model.SessionEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.AccountStatus;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.SessionRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.UserRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class AuthService {

    @Inject
    TokenService tokenService;

    @Inject
    PasswordService passwordService;

    @Inject
    UserRepository userRepository;

    @Inject
    SessionRepository sessionRepository;

    private final Set<String> groups = new HashSet<>();

    public AuthService() {
        groups.add("USER");
    }

    @Transactional
    public TokenPair login(String email, String username, @NotEmpty String password) {
        if ((email == null || email.isEmpty()) && (username == null || username.isEmpty()))
            throw new ApiException(ApiError.LOGIN_NOT_ENOUGH);

        UserEntity user = ((email != null && !email.isEmpty()) ?
                userRepository.findByEmailOptional(email) :
                userRepository.findByUsernameOptional(username))
                .orElseThrow(() -> new ApiException(ApiError.INVALID_CREDENTIALS, "user not found"));
        UserInfo userInfo = new UserInfo(user.userId, user.email, user.username);

        if (!passwordService.verifyPassword(password, user.passwordHash))
            throw new ApiException(ApiError.INVALID_CREDENTIALS, "password incorrect");

        if (user.status == AccountStatus.CREATED)
            throw new ApiException(ApiError.EMAIL_NOT_VERIFIED);

        if (user.status == AccountStatus.SUSPENDED)
            throw new ApiException(ApiError.ACCOUNT_SUSPENDED);

        UUID sessionId = UUID.randomUUID();
        UUID jwtId = UUID.randomUUID();
        Instant issueTime = Instant.now();
        SessionEntity sessionEntity = new SessionEntity(sessionId, jwtId, user);
        sessionRepository.persist(sessionEntity);

        String accessJwt = tokenService.createAccessToken(userInfo, groups, issueTime);
        String refreshJwt = tokenService.creteRefreshToken(userInfo, sessionId, jwtId, issueTime);

        return new TokenPair(accessJwt, refreshJwt, issueTime);
    }

    @Transactional
    public TokenPair refreshToken(String refreshToken) {
        DecodedRefreshToken decodedRefreshToken = tokenService.verifyRefreshToken(refreshToken);
        if (decodedRefreshToken == null)
            throw new ApiException(ApiError.UNAUTHORIZED, "verify failed");

        SessionEntity session = sessionRepository.findByIdOptional(decodedRefreshToken.sessionId())
                .orElseThrow(() -> new ApiException(ApiError.UNAUTHORIZED, "session not found"));

        if (!session.jwtId.equals(decodedRefreshToken.jwtId()) || !session.user.userId.equals(decodedRefreshToken.userId())) {
            System.out.println("DOUBLE USE OF REFRESH TOKEN");
            System.out.println(session.user.userId);
            System.out.println(decodedRefreshToken.userId());
            throw new ApiException(ApiError.UNAUTHORIZED, "double use");
        }

        UserEntity user = session.user;
        UserInfo userInfo = new UserInfo(user.userId, user.email, user.username);

        UUID newJwtId = UUID.randomUUID();
        Instant issueTime = Instant.now();
        session.jwtId = newJwtId;

        String accessJwt = tokenService.createAccessToken(userInfo, groups, issueTime);
        String refreshJwt = tokenService.creteRefreshToken(userInfo, decodedRefreshToken.sessionId(), newJwtId,
                issueTime);
        return new TokenPair(accessJwt, refreshJwt, issueTime);
    }

    @Transactional
    public void logout(String refreshToken) {
        DecodedRefreshToken decodedRefreshToken = tokenService.verifyRefreshToken(refreshToken);
        if (decodedRefreshToken == null)
            throw new ApiException(ApiError.UNAUTHORIZED, "verify failed");

        UUID sessionId = decodedRefreshToken.sessionId();
        if (sessionRepository.deleteById(sessionId) == 0)
            throw new ApiException(ApiError.UNAUTHORIZED, "session not found");
    }

    @Transactional
    public void logoutAll(String refreshToken) {
        DecodedRefreshToken decodedRefreshToken = tokenService.verifyRefreshToken(refreshToken);
        if (decodedRefreshToken == null)
            throw new ApiException(ApiError.UNAUTHORIZED, "verify failed");

        UUID userId = decodedRefreshToken.userId();
        if (sessionRepository.deleteByUserId(userId) == 0)
            throw new ApiException(ApiError.UNAUTHORIZED, "session not found");
    }

}
