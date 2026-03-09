package ru.vlad2509.minionflow.api.dto.request;

import java.util.UUID;

public record TaskCreateRequest(

        UUID jarId,
        UUID inputId,
        UUID configId

) {
}
