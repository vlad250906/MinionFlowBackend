package ru.vlad2509.minionflow.application.dto;

import java.time.Instant;
import java.util.UUID;

public record TokenPair(
        UUID userId,
        String accessJWT,
        String refreshJWT,
        Instant issuedAt
) {
}
