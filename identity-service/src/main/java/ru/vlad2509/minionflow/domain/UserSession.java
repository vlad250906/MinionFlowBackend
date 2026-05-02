package ru.vlad2509.minionflow.domain;

import java.util.Objects;
import java.util.UUID;

public class UserSession {

    private final UUID sessionId;
    private UUID jwtId;
    private final User user;

    public UserSession(UUID sessionId, UUID jwtId, User user) {
        this.sessionId = sessionId;
        this.jwtId = jwtId;
        this.user = user;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getJwtId() {
        return jwtId;
    }

    public User getUser() {
        return user;
    }

    public void setJwtId(UUID jwtId) {
        this.jwtId = jwtId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return Objects.equals(sessionId, that.sessionId) && Objects.equals(jwtId, that.jwtId) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, jwtId, user);
    }
}
