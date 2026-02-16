package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import ru.vlad2509.minionflow.domain.ProjectNameVo;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(nullable = false, unique = true)
    public String projectName;

    @Column(nullable = false)
    public String projectDescription;

    @Column(nullable = false)
    public Instant createdAt;

    public Project() {
    }

    public Project(ProjectNameVo projectName, String projectDescription) {
        this.id = UUID.randomUUID();
        this.projectName = projectName.value();
        this.projectDescription = projectDescription;
        this.createdAt = Instant.now();
    }

    public ProjectNameVo getProjectName() {
        return ProjectNameVo.create(this.projectName);
    }
}
