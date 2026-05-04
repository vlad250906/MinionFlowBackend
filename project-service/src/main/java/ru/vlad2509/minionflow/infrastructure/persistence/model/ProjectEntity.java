package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import ru.vlad2509.minionflow.domain.Project;
import ru.vlad2509.minionflow.domain.vo.ProjectNameVo;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class ProjectEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(nullable = false)
    public String projectName;

    @Column(nullable = false)
    public String projectDescription;

    @Column(nullable = false)
    public Instant createdAt;

    public ProjectEntity() {
    }

    public ProjectEntity(UUID id, String projectName, String projectDescription, Instant createdAt) {
        this.id = id;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.createdAt = createdAt;
    }

    public Project toDomain(){
        return new Project(id, projectName, projectDescription, createdAt);
    }

    public static ProjectEntity fromDomain(Project project){
        return new ProjectEntity(project.getId(), project.getProjectName(), project.getProjectDescription(), project.getCreatedAt());
    }
}
