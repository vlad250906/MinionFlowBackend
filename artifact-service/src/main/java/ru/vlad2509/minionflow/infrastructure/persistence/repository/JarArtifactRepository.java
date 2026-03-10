package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.JarArtifactDto;
import ru.vlad2509.minionflow.domain.model.InputType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Artifact;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifact;
import ru.vlad2509.minionflow.infrastructure.persistence.model.JarArtifact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JarArtifactRepository implements PanacheRepository<JarArtifact> {

    @Inject
    ArtifactRepository artifactRepository;

    @Inject
    EntityManager entityManager;

    @Transactional
    public JarArtifact createJarArtifact(UUID artifactId, String alias) {
        Optional<Artifact> artifactOptional = artifactRepository.findById(artifactId);
        if (artifactOptional.isEmpty())
            return null;

        JarArtifact jarArtifact = new JarArtifact(artifactOptional.get(), alias);
        this.persist(jarArtifact);
        return jarArtifact;
    }

    @Transactional
    public Optional<JarArtifact> update(UUID artifactId, String alias) {
        Optional<JarArtifact> jarArtifactOptional = findByArtifactId(artifactId);
        if (jarArtifactOptional.isEmpty())
            return jarArtifactOptional;

        jarArtifactOptional.get().alias = alias;
        return jarArtifactOptional;
    }


    public Optional<JarArtifact> findByArtifactId(UUID artifactId) {
        return find("artifact.id", artifactId).singleResultOptional();
    }

    // TODO: вот такие штуки могут быть медленными из-за кучи join-ов и количества полей (а нам нужны только некоторые для Light-dto). надо подумать над этим...
    public List<JarArtifact> findAllProjectArtifacts(PaginationContext context, UUID projectId) {
        var query = JarArtifact.find("artifact.projectId", projectId)
                .page(Page.of(context.getPageIndex(), context.getPageSize()));

        context.acceptResult((int) query.count(), query.pageCount());
        return query.list();
    }

}
