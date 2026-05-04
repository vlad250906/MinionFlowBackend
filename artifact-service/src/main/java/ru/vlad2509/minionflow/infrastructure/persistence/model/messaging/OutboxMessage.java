package ru.vlad2509.minionflow.infrastructure.persistence.model.messaging;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "outbox_messages")
public class OutboxMessage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    public String messageId;

    @Column(name = "queue", nullable = false)
    public String queue;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public MessageStatus status;

    @Column(name = "content", columnDefinition = "text", nullable = false)
    public String content;

    @Column(name = "fail_reason", nullable = true)
    public String failReason;

    @Column(name = "leased_by", nullable = false)
    public Integer leasedBy;

    @Column(name = "leased_until", nullable = true, columnDefinition = "timestamptz")
    public Instant leasedUntil;

    @Column(name = "attempts_remaining", nullable = false)
    public Integer attemptsRemaining;

    @Column(name = "next_attempt_at", nullable = true, columnDefinition = "timestamptz")
    public Instant nextAttemptAt;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    public Instant createdAt;

    public OutboxMessage() {
    }

    public OutboxMessage(String messageId, String queue, String content, Integer totalAttempts) {
        this.messageId = messageId;
        this.queue = queue;
        this.content = content;
        this.attemptsRemaining = totalAttempts;

        this.status = MessageStatus.PENDING;
        this.leasedBy = -1;
        this.leasedUntil = null;
        this.nextAttemptAt = null;
        this.failReason = null;
        this.createdAt = Instant.now();
    }
}
