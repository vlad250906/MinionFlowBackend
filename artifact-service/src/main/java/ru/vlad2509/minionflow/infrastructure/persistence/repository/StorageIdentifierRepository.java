package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.infrastructure.persistence.model.StorageIdentifierEntity;

@ApplicationScoped
public class StorageIdentifierRepository implements PanacheRepository<StorageIdentifierEntity> {

    // transactional снаружи
    public void unUse(Long internalId) {
        this.update("usedIn = usedIn - 1 where id = ?1", internalId);
    }

    @Transactional
    public StorageIdentifierEntity create(String storageKey){
        StorageIdentifierEntity identifier = new StorageIdentifierEntity(null, storageKey, false);
        identifier.usedIn++;
        this.persist(identifier);
        return identifier;
    }

    // transactional снаружи
    public void inUse(Long internalId){
        this.update("usedIn = usedIn + 1 where id = ?1", internalId);
    }



}
