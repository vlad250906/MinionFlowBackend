package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "remote_users")
public class RemoteUser extends PanacheEntityBase {

    @Id
    @Column(nullable = false, name = "user_id")
    public UUID userId;

    @Column(nullable = false)
    public String username;

    public RemoteUser() {
    }

    public RemoteUser(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
