package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.domain.Project;
import ru.vlad2509.minionflow.domain.vo.ProjectNameVo;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ProjectEntity;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProjectRepository implements PanacheRepository<ProjectEntity> {

    public Optional<Project> findById(UUID projectId) {
        return find("id", projectId).singleResultOptional().map(ProjectEntity::toDomain);
    }

    public Optional<Project> findByName(ProjectNameVo projectName) {
        return find("projectName", projectName.value()).singleResultOptional().map(ProjectEntity::toDomain);
    }

    @Transactional
    public boolean update(Project project) {
        return this.update("projectName = ?1, projectDescription = ?2 where id = ?3", project.getProjectName(),
                project.getProjectDescription(), project.getId()) > 0;
    }

    @Transactional
    public void create(Project project) {
        this.persist(ProjectEntity.fromDomain(project));
    }

    @Transactional
    public long deleteById(UUID projectId) {
        return delete("id", projectId);
    }


}
