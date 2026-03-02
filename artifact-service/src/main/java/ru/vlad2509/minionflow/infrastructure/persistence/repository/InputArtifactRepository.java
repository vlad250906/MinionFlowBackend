package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.domain.InputType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Artifact;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class InputArtifactRepository implements PanacheRepository<InputArtifact> {

    @Inject
    ArtifactRepository artifactRepository;

    @Inject
    EntityManager entityManager;

    @Transactional
    public InputArtifact createInputArtifact(UUID artifactId, InputType type) {
        Optional<Artifact> artifactOptional = artifactRepository.findById(artifactId);
        if (artifactOptional.isEmpty())
            return null;

        InputArtifact inputArtifact = new InputArtifact(artifactOptional.get(), type);
        this.persist(inputArtifact);
        return inputArtifact;
    }

    @Transactional
    public boolean updateType(UUID artifactId, InputType newType) {
        Optional<InputArtifact> inputArtifactOptional = findByArtifactId(artifactId);
        if (inputArtifactOptional.isEmpty())
            return false;

        inputArtifactOptional.get().type = newType;
        return true;
    }


    public Optional<InputArtifact> findByArtifactId(UUID artifactId) {
        return find("artifact.id", artifactId).singleResultOptional();
    }

    public List<InputArtifact> findAllProjectArtifacts(PaginationContext context, UUID projectId) {
        var query = InputArtifact.find("artifact.projectId", projectId)
                .page(Page.of(context.getPageIndex(), context.getPageSize()));

        context.acceptResult((int) query.count(), query.pageCount());
        return query.list();
    }

}
