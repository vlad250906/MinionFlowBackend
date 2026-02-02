package ru.vlad2509.minionflow.application.dto;

import java.time.Instant;

public record TokenPair(
        String accessJWT,
        String refreshJWT,
        Instant issuedAt
) {
}
