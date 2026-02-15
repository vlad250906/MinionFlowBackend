package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.dto.DecodedRefreshToken;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.infrastructure.persistence.model.SessionEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.SessionRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.UserRepository;

import java.util.UUID;

@ApplicationScoped
public class SessionService {

    @Inject
    UserRepository userRepository;

    @Inject
    SessionRepository sessionRepository;

    @Inject
    TokenService tokenService;

    public SessionEntity getSession(String refreshToken) {
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

        return session;
    }

    @Transactional
    public SessionEntity persistSession(UserEntity user) {
        UUID sessionId = UUID.randomUUID();
        UUID jwtId = UUID.randomUUID();
        SessionEntity sessionEntity = new SessionEntity(sessionId, jwtId, user);
        sessionRepository.persist(sessionEntity);
        return sessionEntity;
    }

    @Transactional
    public boolean logout(SessionEntity session) {
        return sessionRepository.deleteById(session.sessionId) != 0;
    }

    @Transactional
    public boolean logoutAll(UUID userId){
        return sessionRepository.deleteByUserId(userId) != 0;
    }

}
