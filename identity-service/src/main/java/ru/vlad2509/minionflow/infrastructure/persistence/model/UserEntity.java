package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.AccountStatus;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity extends PanacheEntityBase {

    @Id
    public UUID userId;

    @Column(nullable = false, unique = true)
    @Email
    public String email;

    @Column(nullable = false, unique = true)
    @Size(min = 3)
    public String username;

    @Column(nullable = false)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public AccountStatus status;

    public UserEntity() {}

    public UserEntity(String email, String username, String passwordHash) {
        this.userId = UUID.randomUUID();
        this.status = AccountStatus.CREATED;

        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public UserEntity(UUID userId, String email, String username, String passwordHash, AccountStatus status) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
    }
}
