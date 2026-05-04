package ru.vlad2509.minionflow.domain.model;

public class StorageIdentifier {

    private final Long internalId;
    private final String storageKey;
    private final boolean wasDeleted;

    public StorageIdentifier(String storageKey) {
        this.storageKey = storageKey;
        this.internalId = null;
        this.wasDeleted = false;
    }

    public StorageIdentifier(Long internalId, String storageKey, boolean wasDeleted) {
        this.internalId = internalId;
        this.storageKey = storageKey;
        this.wasDeleted = wasDeleted;
    }

    public Long getInternalId() {
        return internalId;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public boolean isWasDeleted() {
        return wasDeleted;
    }
}
