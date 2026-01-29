package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_sessions")
public class SessionEntity extends PanacheEntityBase {

    @Id
    public UUID sessionId;

    @Column(nullable = false, unique = true)
    public UUID jwtId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity user;

    public SessionEntity() {
    }

    public SessionEntity(UUID sessionId, UUID jwtId, UserEntity user) {
        this.sessionId = sessionId;
        this.jwtId = jwtId;
        this.user = user;
    }
}
