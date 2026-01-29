package ru.vlad2509.minionflow.application.dto;

import java.util.UUID;

public record UserInfo(

        UUID userId,
        String email,
        String username

) {
}
