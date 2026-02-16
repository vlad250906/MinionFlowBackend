package ru.vlad2509.minionflow.api.dto.request;

import java.time.Instant;
import java.util.UUID;

public record ProjectMemberRequest(
        UUID userId,
        String memberRole,
        Instant member_since

) {
}
