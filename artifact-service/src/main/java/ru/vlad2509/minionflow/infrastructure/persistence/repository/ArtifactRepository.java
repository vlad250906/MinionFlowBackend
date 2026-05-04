package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ArtifactEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.StorageIdentifierEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ArtifactRepository implements PanacheRepository<ArtifactEntity> {

    @Inject
    StorageIdentifierRepository storageIdentifierRepository;

    @Transactional
    public Optional<Artifact> findById(UUID id) {
        return find("id = ?1", id).singleResultOptional().map(ArtifactEntity::toDomain);
    }

    @Transactional
    public StorageIdentifierEntity create(Artifact artifact) {
        StorageIdentifierEntity storageIdentifierEntity = storageIdentifierRepository.create(artifact.getStorageIdentifier().getStorageKey());
        this.persist(ArtifactEntity.fromDomain(artifact, storageIdentifierEntity));
        return storageIdentifierEntity;
    }

    @Transactional
    public long delete(UUID id) {
        Optional<ArtifactEntity> artifact = find("id", id).singleResultOptional();
        if (artifact.isPresent()) {
            storageIdentifierRepository.unUse(artifact.get().storageIdentifier.id);
            delete(artifact.get());
            return 1;
        }
        return 0;
    }

    @Transactional
    public void updateContentMeta(Artifact artifact) {
        ArtifactEntity entity = find("id = ?1", artifact.getId()).singleResultOptional().orElseThrow(() -> new RuntimeException("Artifact not found"));
        StorageIdentifierEntity old = entity.storageIdentifier;

        entity.contentType = artifact.getContentType();
        entity.size = artifact.getSize();
        entity.originalName = artifact.getOriginalName();

        if (!old.storageKey.equals(artifact.getStorageIdentifier().getStorageKey())) {
            entity.storageIdentifier = storageIdentifierRepository.create(artifact.getStorageIdentifier().getStorageKey());
            storageIdentifierRepository.unUse(old.id);
        }
    }

}
