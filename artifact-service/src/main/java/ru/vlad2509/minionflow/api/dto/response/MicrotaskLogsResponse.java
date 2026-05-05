package ru.vlad2509.minionflow.api.dto.response;

import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLog;

import java.util.List;
import java.util.UUID;

public record MicrotaskLogsResponse(
        UUID microtaskId,
        List<MicrotaskLog> logs
) {
}
