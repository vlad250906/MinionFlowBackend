package ru.vlad2509.minionflow.application.dto.messaging;

import java.util.UUID;

public record UserChange(
        UUID userId,
        String newUsername
) {
}
