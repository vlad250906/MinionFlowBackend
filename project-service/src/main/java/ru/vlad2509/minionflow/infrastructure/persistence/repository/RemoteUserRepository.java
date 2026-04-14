package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vlad2509.minionflow.infrastructure.persistence.model.RemoteUser;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RemoteUserRepository implements PanacheRepository<RemoteUser> {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteUserRepository.class);

    @Transactional
    public Optional<RemoteUser> findByUserId(UUID userId) {
        return find("userId = ?1", userId).singleResultOptional();
    }

    @Transactional
    public Optional<RemoteUser> findByUsername(String username) {
        var query = this.find("username = ?1", username);
        if (query.count() > 1) {
            LOG.warn("Multiple users with the same username found, possible desync: {}", username);
            return Optional.empty();
        }
        return query.singleResultOptional();
    }

    @Transactional
    public void updateOrCreate(UUID userId, String username) {
        Optional<RemoteUser> remoteUserOptional = this.find("userId = ?1", userId).singleResultOptional();
        if (remoteUserOptional.isEmpty()) {
            this.persist(new RemoteUser(userId, username));
            return;
        }

        RemoteUser remoteUser = remoteUserOptional.get();
        remoteUser.username = username;
    }

    @Transactional
    public void delete(UUID userId) {
        this.delete("userId = ?1", userId);
    }

}
