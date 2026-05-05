package ru.vlad2509.minionflow.application.dto.engine.swarm;

import ru.vlad2509.minionflow.application.dto.engine.MicrotaskRunStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SwarmMicrotaskRun(
        UUID taskId,
        UUID microtaskId,
        int displayIndex,
        MicrotaskRunStatus status,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        LocalDateTime runDeadline,
        long runTimeoutSeconds,
        String reason,
        UUID agentId,
        SwarmPhase phase,
        int iteration
) {
}
