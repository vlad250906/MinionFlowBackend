package ru.vlad2509.minionflow.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectMember(
        UUID projectId,
        UUID userId,
        String username,
        String memberRole,
        Instant memberSince

) {
}
