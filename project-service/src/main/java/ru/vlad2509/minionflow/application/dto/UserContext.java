package ru.vlad2509.minionflow.application.dto;

import java.util.UUID;

public record UserContext(
        UUID userId,
        String username,
        String email
) {
}
