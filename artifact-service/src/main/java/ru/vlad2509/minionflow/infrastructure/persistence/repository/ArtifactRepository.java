package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.domain.model.ArtifactType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Artifact;
import ru.vlad2509.minionflow.infrastructure.persistence.model.StorageIdentifier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ArtifactRepository implements PanacheRepository<Artifact> {

    @Inject
    StorageIdentifierRepository storageIdentifierRepository;

    public Optional<Artifact> findById(UUID id) {
        return find("id = ?1", id).singleResultOptional();
    }

    @Transactional
    public Artifact create(UUID projectId, UUID userId, ArtifactType type, String alias, long size, String originalName,
                           String contentType, String storageKey) {
        Artifact artifact = new Artifact(projectId, userId, type, alias, size,
                originalName, contentType, storageIdentifierRepository.create(storageKey));
        this.persist(artifact);
        return artifact;
    }

    public List<Artifact> findAllProjectArtifacts(PaginationContext context, UUID projectId, ArtifactType type) {
        var query = find("projectId = ?1 and type = ?2", projectId, type);
        query.page(Page.of(context.getPageIndex(), context.getPageSize()));
        context.acceptResult((int) query.count(), query.pageCount());
        return query.list();
    }

    @Transactional
    public long delete(UUID id) {
        Optional<Artifact> artifact = find("id", id).singleResultOptional();
        if (artifact.isPresent()) {
            storageIdentifierRepository.unUse(artifact.get().storageIdentifier);
            delete(artifact.get());
            return 1;
        }
        return 0;
    }

    public void updateIdentifier(Artifact artifact, String newStorageKey){
        StorageIdentifier old = artifact.storageIdentifier;
        artifact.storageIdentifier = storageIdentifierRepository.create(newStorageKey);
        storageIdentifierRepository.unUse(old);
    }

}
