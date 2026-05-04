package ru.vlad2509.minionflow.domain;

import ru.vlad2509.minionflow.domain.vo.ProjectNameVo;

import java.time.Instant;
import java.util.UUID;

public class Project {

    private final UUID id;
    private String projectName;
    private String projectDescription;
    private final Instant createdAt;

    public Project(ProjectNameVo projectName, String projectDescription) {
        this.projectName = projectName.value();
        this.projectDescription = projectDescription;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public Project(UUID id, String projectName, String projectDescription, Instant createdAt) {
        this.id = id;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public ProjectNameVo getProjectNameVo() {
        return ProjectNameVo.create(this.projectName);
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setProjectName(ProjectNameVo projectNameVo) {
        this.projectName = projectNameVo.value();
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
}
