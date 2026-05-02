package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.vlad2509.minionflow.domain.VerificationTicket;
import ru.vlad2509.minionflow.infrastructure.persistence.model.VerificationTicketEntity;
import ru.vlad2509.minionflow.domain.enums.VerificationTicketType;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class VerificationTicketRepository implements PanacheRepository<VerificationTicketEntity> {

    public Optional<VerificationTicket> findByUserAndType(UUID userId, VerificationTicketType type){
        return find("user.userId = ?1 and type = ?2", userId, type).singleResultOptional().map(VerificationTicketEntity::toDomain);
    }

    public void create(VerificationTicket verificationTicket){
        this.persist(VerificationTicketEntity.fromDomain(verificationTicket));
    }

    public void delete(Long id){
        this.delete("id = ?1", id);
    }

}
