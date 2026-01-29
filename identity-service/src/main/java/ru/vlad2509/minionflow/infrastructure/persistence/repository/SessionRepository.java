package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.infrastructure.persistence.model.SessionEntity;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SessionRepository implements PanacheRepository<SessionEntity> {

    public Optional<SessionEntity> findByIdOptional(UUID sessionId) {
        return find("sessionId", sessionId).singleResultOptional();
    }

    public long deleteById(UUID sessionId) {
        return delete("sessionId", sessionId);
    }

    public long deleteByUserId(UUID userId) {
        return delete("user.userId", userId);
    }

}
