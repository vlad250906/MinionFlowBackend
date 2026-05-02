package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.domain.UserSession;
import ru.vlad2509.minionflow.infrastructure.persistence.model.SessionEntity;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SessionRepository implements PanacheRepository<SessionEntity> {

    public Optional<UserSession> findByIdOptional(UUID sessionId) {
        return find("sessionId", sessionId).singleResultOptional().map(SessionEntity::toDomain);
    }

    @Transactional
    public long deleteById(UUID sessionId) {
        return delete("sessionId", sessionId);
    }

    @Transactional
    public long deleteByUserId(UUID userId) {
        return delete("user.userId", userId);
    }

    @Transactional
    public boolean updateJwt(UUID sessionId, UUID jwtId){
        return update("jwtId = ?1 where sessionId = ?2", jwtId, sessionId) > 0;
    }



}
