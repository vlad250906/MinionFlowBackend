package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.infrastructure.persistence.model.StorageIdentifier;

@ApplicationScoped
public class StorageIdentifierRepository implements PanacheRepository<StorageIdentifier> {

    // transactional снаружи
    public void unUse(StorageIdentifier identifier) {
        identifier.usedIn--;
        if (identifier.usedIn < 0) {
            //TODO: warn в логах или типа того
        }
    }

    @Transactional
    public StorageIdentifier create(String storageKey){
        StorageIdentifier identifier = new StorageIdentifier(storageKey);
        identifier.usedIn++;
        this.persist(identifier);
        return identifier;
    }

    // transactional снаружи
    public void inUse(StorageIdentifier identifier){
        identifier.usedIn++;
    }



}
