package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.domain.vo.EmailVo;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;
import ru.vlad2509.minionflow.infrastructure.persistence.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {

    public Optional<UserEntity> findByEmailOptional(EmailVo email) {
        return find("email", email.value()).singleResultOptional();
    }

    public Optional<UserEntity> findByUsernameOptional(UsernameVo username) {
        return find("username", username.value()).singleResultOptional();
    }

    public Optional<UserEntity> findByIdOptional(UUID userId) {
        return find("userId", userId).singleResultOptional();
    }
}
