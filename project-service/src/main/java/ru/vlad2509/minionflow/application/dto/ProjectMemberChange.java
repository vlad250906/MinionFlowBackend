package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.MemberRole;

import java.util.UUID;

public record ProjectMemberChange(
        UUID projectId,
        UUID userId,
        MemberRole newMemberRole
) {
}
