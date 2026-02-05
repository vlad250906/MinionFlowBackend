package ru.vlad2509.minionflow.api.auth.dto.response;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse (
        UUID accountId,
        String accessJWT,
        Instant validUntil
) {
}
