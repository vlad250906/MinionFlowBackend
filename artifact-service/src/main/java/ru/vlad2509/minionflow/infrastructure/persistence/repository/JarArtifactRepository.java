package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.domain.model.InputArtifact;
import ru.vlad2509.minionflow.domain.model.JarArtifact;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ArtifactEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifactEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.JarArtifactEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JarArtifactRepository implements PanacheRepository<JarArtifactEntity> {

    @Inject
    ArtifactRepository artifactRepository;

    @Inject
    EntityManager em;

    @Transactional
    public void createJarArtifact(JarArtifact jarArtifact) {
        ArtifactEntity artifactEntity = artifactRepository.getEntityManager().find(ArtifactEntity.class, jarArtifact.getId());
        JarArtifactEntity jarArtifactEntity = JarArtifactEntity.fromDomain(jarArtifact, artifactEntity);
        this.persist(jarArtifactEntity);
    }

    @Transactional
    public void update(JarArtifact jarArtifact) {
        this.update("alias = ?1 where id = ?2", jarArtifact.getAlias(), jarArtifact.getInternalId());
    }

    @Transactional
    public Optional<JarArtifact> findByArtifactId(UUID artifactId) {
        return find("artifact.id", artifactId).singleResultOptional().map(JarArtifactEntity::toDomain);
    }

    @Transactional
    public List<JarArtifact> findAllProjectArtifacts(PaginationContext context, UUID projectId) {
        var query = this.find("artifact.projectId", projectId)
                .page(Page.of(context.getPageIndex(), context.getPageSize()));

        context.acceptResult((int) query.count(), query.pageCount());
        return query.stream().map(JarArtifactEntity::toDomain).toList();
    }

}
