package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.infrastructure.persistence.model.enums.AccountStatus;

import java.util.UUID;

public record UserInfo(

        UUID userId,
        String email,
        String username,
        AccountStatus status

) {
}
