package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.UserSession;

import java.util.UUID;

@Entity
@Table(name = "user_sessions")
public class SessionEntity extends PanacheEntityBase {

    @Id
    @Column(name = "session_id")
    public UUID sessionId;

    @Column(name = "jwt_id", nullable = false, unique = true)
    public UUID jwtId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public UserEntity user;

    public SessionEntity() {
    }

    public SessionEntity(UUID sessionId, UUID jwtId, UserEntity user) {
        this.sessionId = sessionId;
        this.jwtId = jwtId;
        this.user = user;
    }

    public UserSession toDomain(){
        return new UserSession(sessionId, jwtId, user.toDomain());
    }
}
