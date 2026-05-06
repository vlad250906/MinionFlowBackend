package ru.vlad2509.minionflow.application.dto.engine;

import java.util.List;
import java.util.UUID;

public record MicrotaskLogsBatch(
        UUID microtaskId,
        List<MicrotaskLog> logs
) {
}
