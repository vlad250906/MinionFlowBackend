package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifact;
import ru.vlad2509.minionflow.infrastructure.persistence.model.TaskRun;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TaskRunRepository implements PanacheRepository<TaskRun> {

    public Optional<TaskRun> findById(UUID id) {
        return find("id", id).singleResultOptional();
    }

    public List<TaskRun> findAllTasks(PaginationContext context, UUID projectId) {
        var query = TaskRun.find("projectId", projectId)
                .page(Page.of(context.getPageIndex(), context.getPageSize()));

        context.acceptResult((int) query.count(), query.pageCount());
        return query.list();
    }

}
