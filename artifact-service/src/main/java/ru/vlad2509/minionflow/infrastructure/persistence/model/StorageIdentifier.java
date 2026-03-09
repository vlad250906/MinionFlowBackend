package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "storage_identifiers")
public class StorageIdentifier extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // public String bucket;

    @Column(nullable = false, unique = true)
    public String storageKey;

    @Column(nullable = false)
    public int usedIn;

    @Column(nullable = false)
    public boolean wasDeleted;

    public StorageIdentifier() {
    }

    public StorageIdentifier(String storageKey) {
        this.storageKey = storageKey;

        this.wasDeleted = false;
        this.usedIn = 0;
    }
}
