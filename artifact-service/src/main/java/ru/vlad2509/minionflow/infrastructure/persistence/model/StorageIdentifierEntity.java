package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import ru.vlad2509.minionflow.domain.model.StorageIdentifier;

@Entity
@Table(name = "storage_identifiers")
public class StorageIdentifierEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // public String bucket;

    @Column(name = "storage_key", nullable = false, unique = true)
    public String storageKey;

    @Column(name = "used_in", nullable = false)
    public int usedIn;

    @Column(name = "was_deleted", nullable = false)
    public boolean wasDeleted;

    public StorageIdentifierEntity() {
    }

    public StorageIdentifierEntity(Long id, String storageKey, boolean wasDeleted) {
        this.id = id;
        this.storageKey = storageKey;
        this.wasDeleted = wasDeleted;
        this.usedIn = 0;
    }

    public StorageIdentifier toDomain(){
        return new StorageIdentifier(id, storageKey, wasDeleted);
    }

    public static StorageIdentifierEntity fromDomain(StorageIdentifier storageIdentifier){
        return new StorageIdentifierEntity(storageIdentifier.getInternalId(), storageIdentifier.getStorageKey(), storageIdentifier.isWasDeleted());
    }
}
