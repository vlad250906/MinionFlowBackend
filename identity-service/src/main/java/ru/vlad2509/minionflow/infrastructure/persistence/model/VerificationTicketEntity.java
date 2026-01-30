package ru.vlad2509.minionflow.infrastructure.persistence.model;


import io.quarkus.Generated;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.VerificationTicketType;

import java.util.UUID;

@Entity
@Table(name = "verification_tickets")
public class VerificationTicketEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    long id;

    @Column(nullable = false)
    public UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public VerificationTicketType type;

    @Column(nullable = false)
    public UUID verificationToken;

    public VerificationTicketEntity() {
    }

    public VerificationTicketEntity(UUID userId, VerificationTicketType type, UUID verificationToken) {
        this.userId = userId;
        this.type = type;
        this.verificationToken = verificationToken;
    }
}
