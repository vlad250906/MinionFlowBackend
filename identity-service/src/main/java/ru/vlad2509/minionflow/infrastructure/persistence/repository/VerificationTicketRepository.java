package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.infrastructure.persistence.model.VerificationTicketEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.VerificationTicketType;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class VerificationTicketRepository implements PanacheRepository<VerificationTicketEntity> {

    public Optional<VerificationTicketEntity> findByUserAndType(UUID userId, VerificationTicketType type){
        return find("userId = ?1 and type = ?2", userId, type).singleResultOptional();
    }

}
