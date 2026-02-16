package ru.vlad2509.minionflow.api.dto.request;

import java.time.Instant;

public record ProjectMemberAddRequest(
        String username,
        String memberRole,
        Instant member_since
) {
}
