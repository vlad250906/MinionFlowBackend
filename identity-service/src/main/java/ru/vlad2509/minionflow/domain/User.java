package ru.vlad2509.minionflow.domain;

import ru.vlad2509.minionflow.domain.enums.AccountStatus;
import ru.vlad2509.minionflow.domain.vo.UsernameVo;

import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private String username;
    private String passwordHash;
    private AccountStatus status;

    public User(String email, String username, String passwordHash){
        this.id = UUID.randomUUID();
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = AccountStatus.CREATED;
    }

    public User(UUID id, String email, String username, String passwordHash, AccountStatus status) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setUsername(UsernameVo usernameVo) {
        this.username = usernameVo.value();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setStatus(AccountStatus status) {
        if (!this.status.mayUpdateTo(status))
            throw new IllegalArgumentException("Status cannot be downgraded");
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(username, user.username) && Objects.equals(passwordHash, user.passwordHash) && status == user.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username, passwordHash, status);
    }
}
