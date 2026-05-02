package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.domain.User;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {

    @Inject
    EntityManager em;

    public Optional<User> findByEmailOptional(EmailVo email) {
        return find("email", email.value()).singleResultOptional().map(UserEntity::toDomain);
    }

    public Optional<User> findByUsernameOptional(UsernameVo username) {
        return find("username", username.value()).singleResultOptional().map(UserEntity::toDomain);
    }

    public Optional<User> findByIdOptional(UUID userId) {
        return find("userId", userId).singleResultOptional().map(UserEntity::toDomain);
    }

    @Transactional
    public boolean updateUsername(User user) {
        return this.update("username = ?1 where id = ?2", user.getUsername(), user.getId()) > 0;
    }

    @Transactional
    public boolean updatePasswordHash(User user) {
        return this.update("passwordHash = ?1 where id = ?2", user.getPasswordHash(), user.getId()) > 0;
    }

    @Transactional
    public boolean updateStatus(User user) {
        return this.update("status = ?1 where id = ?2", user.getStatus(), user.getId()) > 0;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean create(User user) {
        try {
            this.persist(new UserEntity(user.getId(), user.getEmail(), user.getUsername(), user.getPasswordHash(), user.getStatus()));
            this.flush();
            return true;
        } catch (PersistenceException ex) {
            return false;
        }
    }
}
