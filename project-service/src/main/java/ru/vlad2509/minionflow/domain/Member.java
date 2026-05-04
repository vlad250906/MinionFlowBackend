package ru.vlad2509.minionflow.domain;

import ru.vlad2509.minionflow.domain.enums.MemberRole;

import java.time.Instant;
import java.util.UUID;

public class Member {

    private final Long internalId;
    private final Project project;
    private final UUID userId;
    private final String remoteUsername;
    private MemberRole role;
    private final Instant memberSince;

    public Member(Project project, UUID userId, MemberRole role, String remoteUsername) {
        this.project = project;
        this.userId = userId;
        this.role = role;
        this.remoteUsername = remoteUsername;

        this.internalId = null;
        this.memberSince = Instant.now();
    }

    public Member(Long internalId, Project project, UUID userId, String remoteUsername, MemberRole role, Instant memberSince) {
        this.internalId = internalId;
        this.project = project;
        this.userId = userId;
        this.remoteUsername = remoteUsername;
        this.role = role;
        this.memberSince = memberSince;
    }

    public Long getInternalId() {
        return internalId;
    }

    public Project getProject() {
        return project;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRemoteUsername() {
        return remoteUsername;
    }

    public MemberRole getRole() {
        return role;
    }

    public Instant getMemberSince() {
        return memberSince;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }
}
