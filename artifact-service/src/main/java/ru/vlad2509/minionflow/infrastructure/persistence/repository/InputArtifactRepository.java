package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.domain.model.InputArtifact;
import ru.vlad2509.minionflow.domain.model.enums.InputType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ArtifactEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifactEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class InputArtifactRepository implements PanacheRepository<InputArtifactEntity> {

    @Inject
    EntityManager em;

    @Inject
    ArtifactRepository artifactRepository;

    @Transactional
    public void createInputArtifact(InputArtifact inputArtifact) {
        ArtifactEntity artifactEntity = em.find(ArtifactEntity.class, inputArtifact.getId());
        InputArtifactEntity inputArtifactEntity = InputArtifactEntity.fromDomain(inputArtifact, artifactEntity);
        this.persist(inputArtifactEntity);
    }

    @Transactional
    public void update(InputArtifact inputArtifact) {
        this.update("alias = ?1, type = ?2 where id = ?3", inputArtifact.getAlias(), inputArtifact.getInputType(), inputArtifact.getInternalId());
    }


    public Optional<InputArtifact> findByArtifactId(UUID artifactId) {
        return find("artifact.id", artifactId).singleResultOptional().map(InputArtifactEntity::toDomain);
    }

    // TODO: вот такие штуки могут быть медленными из-за кучи join-ов и количества полей (а нам нужны только некоторые для Light-dto). надо подумать над этим...
    // upd: пох
    public List<InputArtifact> findAllProjectArtifacts(PaginationContext context, UUID projectId) {
        var query = this.find("artifact.projectId", projectId)
                .page(Page.of(context.getPageIndex(), context.getPageSize()));

        context.acceptResult((int) query.count(), query.pageCount());
        return query.stream().map(InputArtifactEntity::toDomain).toList();
    }

}
