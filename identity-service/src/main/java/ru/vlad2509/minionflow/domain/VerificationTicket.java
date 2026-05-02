package ru.vlad2509.minionflow.domain;

import ru.vlad2509.minionflow.domain.enums.VerificationTicketType;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class VerificationTicket {

    private final Long internalId;
    private final User user;
    private final VerificationTicketType type;
    private final UUID verificationToken;
    private final Instant validFrom;
    private final Instant validTo;

    public VerificationTicket(User user, VerificationTicketType type, UUID verificationToken, long ttlSeconds) {
        this(null, user, type, verificationToken, Instant.now(), Instant.now().plusSeconds(ttlSeconds));
    }

    public VerificationTicket(Long internalId, User user, VerificationTicketType type, UUID verificationToken, Instant validFrom, Instant validTo) {
        this.internalId = internalId;
        this.user = user;
        this.type = type;
        this.verificationToken = verificationToken;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Long getInternalId() {
        return internalId;
    }

    public User getUser() {
        return user;
    }

    public VerificationTicketType getType() {
        return type;
    }

    public UUID getVerificationToken() {
        return verificationToken;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public Instant getValidTo() {
        return validTo;
    }

    public boolean isValid(UUID token){
        Instant now = Instant.now();
        return validFrom.isBefore(now) && validTo.isAfter(now) && this.verificationToken.equals(token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationTicket that = (VerificationTicket) o;
        return Objects.equals(internalId, that.internalId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(internalId);
    }
}
