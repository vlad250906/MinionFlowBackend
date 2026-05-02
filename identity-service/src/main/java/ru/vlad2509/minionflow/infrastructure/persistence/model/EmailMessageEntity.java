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
    @Column(name = "id")
    public long id;

    @Column(name = "email", nullable = false)
    @Email
    public String email;

    @Column(name = "subject", columnDefinition = "text", nullable = false)
    public String subject;

    @Column(name = "content", columnDefinition = "text", nullable = false)
    public String content;

    @Column(name = "is_sent", nullable = false)
    public boolean isSent;

    @Column(name = "is_failed", nullable = false)
    public boolean isFailed;

    @Column(name = "fail_reason", nullable = true)
    public String failReason;

    @Column(name = "taken_by", nullable = false)
    public int takenBy;

    @Column(name = "taken_until", nullable = false, columnDefinition = "timestamptz")
    public Instant takenUntil;

    @Column(name = "attempts", nullable = false)
    public int attempts;

    @Column(name = "next_try_in", nullable = false, columnDefinition = "timestamptz")
    public Instant nextTryIn;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    public Instant createdAt;

    public EmailMessageEntity() {
    }

    public EmailMessageEntity(String email, String subject, String content, int attempts) {
        this.email = email;
        this.subject = subject;
        this.content = content;
        this.attempts = attempts;

        this.isSent = false;
        this.isFailed = false;
        this.takenBy = -1;
        this.takenUntil = Instant.now();
        this.nextTryIn = Instant.now();
        this.createdAt = Instant.now();
    }
}
