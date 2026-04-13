package ru.vlad2509.minionflow.infrastructure.persistence.model.messaging;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "inbox_messages")
public class InboxMessage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    public Long id;

    @Column(nullable = false, unique = true)
    public String messageId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public MessageStatus status;

    @Column(nullable = true)
    public String failReason;

    @Column(nullable = false)
    public Integer attemptsRemaining;

    public InboxMessage() {
    }

    public InboxMessage(String messageId, int attemptsRemaining) {
        this.messageId = messageId;
        this.status = MessageStatus.PENDING;
        this.attemptsRemaining = attemptsRemaining;

        this.failReason = null;
    }
}
