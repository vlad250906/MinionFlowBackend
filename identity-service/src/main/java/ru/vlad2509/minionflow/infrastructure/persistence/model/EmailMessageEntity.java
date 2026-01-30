package ru.vlad2509.minionflow.infrastructure.persistence.model;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "email_messages")
public class EmailMessageEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public long id;

    @Column(nullable = false)
    @Email
    public String email;

    @Column(nullable = false)
    public String content;

    @Column(nullable = false)
    public boolean isSent;

    @Column(nullable = false)
    public boolean isFailed;

    @Column(nullable = false)
    public int takenBy;

    @Column(nullable = false, columnDefinition = "timestamptz")
    public Instant takenUntil;

    @Column(nullable = false)
    public int attempts;

    @Column(nullable = false, columnDefinition = "timestamptz")
    public Instant nextTryIn;

    public EmailMessageEntity() {
    }

    public EmailMessageEntity(String email, String content, int attempts) {
        this.email = email;
        this.content = content;
        this.attempts = attempts;

        this.isSent = false;
        this.isFailed = false;
        this.takenBy = -1;
        this.takenUntil = Instant.now();
        this.nextTryIn = Instant.now();
    }
}
