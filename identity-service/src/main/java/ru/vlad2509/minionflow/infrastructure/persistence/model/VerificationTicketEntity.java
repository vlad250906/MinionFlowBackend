package ru.vlad2509.minionflow.infrastructure.persistence.model;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.VerificationTicket;
import ru.vlad2509.minionflow.domain.enums.VerificationTicketType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verification_tickets")
public class VerificationTicketEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    @Column(name = "id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public VerificationTicketType type;

    @Column(name = "verification_token", nullable = false)
    public UUID verificationToken;

    @Column(name = "valid_from", nullable = false, columnDefinition = "timestamptz")
    public Instant validFrom;

    @Column(name = "valid_to", nullable = false, columnDefinition = "timestamptz")
    public Instant validTo;

    public VerificationTicketEntity() {
    }

    public VerificationTicketEntity(Long id, UserEntity user, VerificationTicketType type, UUID verificationToken, Instant validFrom, Instant validTo) {
        this.user = user;
        this.type = type;
        this.verificationToken = verificationToken;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.id = id;
    }

    public VerificationTicket toDomain() {
        return new VerificationTicket(id, user.toDomain(), type, verificationToken, validFrom, validTo);
    }

    public static VerificationTicketEntity fromDomain(VerificationTicket verificationTicket) {
        return new VerificationTicketEntity(verificationTicket.getInternalId(),
                UserEntity.fromDomain(verificationTicket.getUser()),
                verificationTicket.getType(), verificationTicket.getVerificationToken(),
                verificationTicket.getValidFrom(), verificationTicket.getValidTo());
    }
}
