package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Artifact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ArtifactRepository implements PanacheRepository<Artifact> {

    public Optional<Artifact> findById(UUID id) {
        return find("id = ?1 and markDeleted = false", id).singleResultOptional();
    }

    public List<Artifact> findAllProjectArtifacts(PaginationContext context, UUID projectId){
        var query = find("projectId = ?1 and markDeleted = false", projectId);
        query.page(Page.of(context.getPageIndex(), context.getPageSize()));
        context.acceptResult((int) query.count(), query.pageCount());
        return query.list();
    }

    @Transactional
    public long hardDelete(UUID id){
        return delete("id", id);
    }

}
