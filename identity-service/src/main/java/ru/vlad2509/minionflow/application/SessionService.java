package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.application.dto.DecodedRefreshToken;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.domain.User;
import ru.vlad2509.minionflow.domain.UserSession;
import ru.vlad2509.minionflow.infrastructure.persistence.model.SessionEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.SessionRepository;

import java.util.UUID;

@ApplicationScoped
public class SessionService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);

    @Inject
    SessionRepository sessionRepository;

    @Inject
    TokenService tokenService;

    public UserSession getSession(String refreshToken) {
        DecodedRefreshToken decodedRefreshToken = tokenService.verifyRefreshToken(refreshToken);
        if (decodedRefreshToken == null)
            throw new ApiException(ApiError.UNAUTHORIZED, "verify failed");

        UserSession session = sessionRepository.findByIdOptional(decodedRefreshToken.sessionId())
                .orElseThrow(() -> new ApiException(ApiError.UNAUTHORIZED, "session not found"));

        if (!session.getJwtId().equals(decodedRefreshToken.jwtId()) || !session.getUser().getId().equals(decodedRefreshToken.userId())) {
            LOG.error("JWT use after refresh for user with id: {}", session.getUser().getId());
            throw new ApiException(ApiError.UNAUTHORIZED, "double use");
        }

        return session;
    }

    @Transactional
    public UserSession persistSession(User user) {
        UUID sessionId = UUID.randomUUID();
        UUID jwtId = UUID.randomUUID();
        SessionEntity sessionEntity = new SessionEntity(sessionId, jwtId, UserEntity.fromDomain(user));
        sessionRepository.persist(sessionEntity);
        return sessionEntity.toDomain();
    }

    @Transactional
    public void update(UserSession session) {
        sessionRepository.updateJwt(session.getSessionId(), session.getJwtId());
    }

    @Transactional
    public boolean logout(UserSession session) {
        return sessionRepository.deleteById(session.getSessionId()) != 0;
    }

    @Transactional
    public boolean logoutAll(UUID userId) {
        return sessionRepository.deleteByUserId(userId) != 0;
    }

}
