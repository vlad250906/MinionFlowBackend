package ru.vlad2509.minionflow.domain.model.enums;

import java.util.Set;

import static ru.vlad2509.minionflow.domain.model.enums.ProjectPermission.*;

public enum MemberRole {
    OWNER(NONE, JAR_WRITE, JAR_READ, JAR_DOWNLOAD, INPUT_WRITE, INPUT_READ, CONFIG_READ, CONFIG_WRITE, TASK_WRITE, TASK_READ, OUTPUT_READ, LOG_READ),
    MAINTAINER(NONE, JAR_WRITE, JAR_READ, JAR_DOWNLOAD, INPUT_WRITE, INPUT_READ, CONFIG_READ, CONFIG_WRITE, TASK_WRITE, TASK_READ, OUTPUT_READ, LOG_READ),
    USER(NONE, INPUT_WRITE, INPUT_READ, TASK_WRITE, TASK_READ, OUTPUT_READ, JAR_READ, CONFIG_READ, LOG_READ);

    private final Set<ProjectPermission> permissions;

    MemberRole(ProjectPermission... permissions) {
        this.permissions = Set.of(permissions);
    }

    public Set<ProjectPermission> getPermissions() {
        return permissions;
    }
}
