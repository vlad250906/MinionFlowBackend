package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.domain.ProjectNameVo;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Project;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProjectRepository implements PanacheRepository<Project> {

    public Optional<Project> findById(UUID projectId) {
        return find("id", projectId).singleResultOptional();
    }

    public Optional<Project> findByName(ProjectNameVo projectName) {
        return find("projectName", projectName.value()).singleResultOptional();
    }

    public long deleteById(UUID projectId) {
        return delete("id", projectId);
    }


}
