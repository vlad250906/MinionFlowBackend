package ru.vlad2509.minionflow.application.dto.engine.swarm;

import ru.vlad2509.minionflow.application.dto.engine.EngineTaskStatus;
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;

import java.util.List;
import java.util.UUID;

public record SwarmTaskState(

        UUID taskId,
        long seq,
        String kind,
        EngineTaskStatus status,
        TaskStatus taskStatus,
        SwarmTaskSummary summary,
        List<SwarmAgentState> microtasks

) {
}
