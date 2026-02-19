package ru.vlad2509.minionflow.application.context;

import java.util.UUID;

public record UserContext(
        UUID userId,
        String username,
        String email
) {
}
