package ru.vlad2509.minionflow.domain.enums;

import java.util.Set;

import static ru.vlad2509.minionflow.domain.enums.ProjectPermission.*;

public enum MemberRole {
    OWNER(NONE, PROJECT_UPDATE_GENERAL, PROJECT_DELETE, PROJECT_READ, PROJECT_MEMBER_UPDATE, PROJECT_MEMBER_ADD_DELETE),
    MAINTAINER(NONE, PROJECT_READ),
    USER(NONE, PROJECT_READ);

    private final Set<ProjectPermission> permissions;

    MemberRole(ProjectPermission... permissions) {
        this.permissions = Set.of(permissions);
    }

    public Set<ProjectPermission> getPermissions() {
        return permissions;
    }
}
