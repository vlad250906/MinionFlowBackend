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
import ru.vlad2509.minionflow.domain.User;
import ru.vlad2509.minionflow.domain.UserSession;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;
import ru.vlad2509.minionflow.domain.enums.AccountStatus;
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
        User user = ((email != null) ?
                userRepository.findByEmailOptional(email) :
                userRepository.findByUsernameOptional(username))
                .orElseThrow(() -> new ApiException(ApiError.INVALID_CREDENTIALS, "user not found"));
        UserInfo userInfo = new UserInfo(user.getId(), user.getEmail(), user.getUsername(), user.getStatus());

        if (!passwordService.verifyPassword(password, user.getPasswordHash()))
            throw new ApiException(ApiError.INVALID_CREDENTIALS, "password incorrect");

        if (user.getStatus() == AccountStatus.CREATED)
            throw new ApiException(ApiError.EMAIL_NOT_VERIFIED);

        if (user.getStatus() == AccountStatus.SUSPENDED)
            throw new ApiException(ApiError.ACCOUNT_SUSPENDED);

        Instant issueTime = Instant.now();
        UserSession session = sessionService.persistSession(user);

        String accessJwt = tokenService.createAccessToken(userInfo, groups, issueTime);
        String refreshJwt = tokenService.creteRefreshToken(userInfo, session.getSessionId(), session.getJwtId(), issueTime);

        return new TokenPair(user.getId(), accessJwt, refreshJwt, issueTime);
    }

    @Transactional
    public TokenPair refreshToken(String refreshToken) {
        UserSession session = sessionService.getSession(refreshToken);
        User user = session.getUser();
        UserInfo userInfo = UserInfo.fromDomain(user);

        UUID newJwtId = UUID.randomUUID();
        Instant issueTime = Instant.now();
        session.setJwtId(newJwtId);
        sessionService.update(session);

        String accessJwt = tokenService.createAccessToken(userInfo, groups, issueTime);
        String refreshJwt = tokenService.creteRefreshToken(userInfo, session.getSessionId(), newJwtId, issueTime);
        return new TokenPair(user.getId(), accessJwt, refreshJwt, issueTime);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (!sessionService.logout(sessionService.getSession(refreshToken)))
            throw new ApiException(ApiError.UNAUTHORIZED, "session not found");
    }

    @Transactional
    public void logoutAll(String refreshToken) {
        if (!sessionService.logoutAll(sessionService.getSession(refreshToken).getUser().getId()))
            throw new ApiException(ApiError.UNAUTHORIZED, "session not found");
    }

}
