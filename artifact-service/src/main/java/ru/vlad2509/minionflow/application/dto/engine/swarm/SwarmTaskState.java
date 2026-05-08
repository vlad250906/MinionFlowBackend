package ru.vlad2509.minionflow.application.dto.engine.swarm;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.vlad2509.minionflow.application.dto.engine.BaseTaskState;
import ru.vlad2509.minionflow.application.dto.engine.EngineTaskStatus;
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SwarmTaskState (

        UUID taskId,
        long seq,
        String kind,
        EngineTaskStatus status,
        TaskStatus taskStatus,
        SwarmTaskSummary summary,
        List<SwarmAgentState> microtasks

) implements BaseTaskState {
}
