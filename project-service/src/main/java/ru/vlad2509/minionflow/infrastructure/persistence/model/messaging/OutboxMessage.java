package ru.vlad2509.minionflow.infrastructure.persistence.model.messaging;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "outbox_messages")
public class OutboxMessage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    public Long id;

    @Column(nullable = false, unique = true)
    public String messageId;

    @Column(nullable = false)
    public String queue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public MessageStatus status;

    @Column(nullable = false)
    public String content;

    @Column(nullable = true)
    public String failReason;

    @Column(nullable = false)
    public Integer leasedBy;

    @Column(nullable = true)
    public Instant leasedUntil;

    @Column(nullable = false)
    public Integer attemptsRemaining;

    @Column(nullable = true)
    public Instant nextAttemptAt;

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
    }
}
