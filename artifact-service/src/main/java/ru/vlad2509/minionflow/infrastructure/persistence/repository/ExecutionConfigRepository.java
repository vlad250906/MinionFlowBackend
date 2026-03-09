package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfig;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ExecutionConfigJpa;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ExecutionConfigRepository implements PanacheRepository<ExecutionConfigJpa> {

    public Optional<ExecutionConfigJpa> findById(UUID id) {
        return find("id", id).singleResultOptional();
    }

    @Transactional
    public long deleteById(UUID id) {
        return delete("id", id);
    }

    public List<ExecutionConfigJpa> findAllProjectConfigs(PaginationContext context, UUID projectId) {
        var query = find("projectId = ?1", projectId);
        query.page(Page.of(context.getPageIndex(), context.getPageSize()));
        context.acceptResult((int) query.count(), query.pageCount());
        return query.list();
    }

    @Transactional
    public void createExecutionConfig(ExecutionConfigJpa executionConfigJpa) {
        this.persist(executionConfigJpa);
    }
}
