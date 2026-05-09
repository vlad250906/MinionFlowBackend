package ru.vlad2509.minionflow.infrastructure.engine.dto;

import jakarta.validation.Valid;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionType;
import ru.vlad2509.minionflow.domain.model.execution.TimeoutsSpec;
import ru.vlad2509.minionflow.domain.model.execution.retry.RetrySpec;
import ru.vlad2509.minionflow.domain.model.execution.scheduling.SchedulingSpec;
import ru.vlad2509.minionflow.domain.model.execution.swarm.SwarmSpec;
import ru.vlad2509.minionflow.domain.model.execution.worker.WorkerSpec;

public record EngineExecutionSpec(
        ExecutionType type,
        SchedulingSpec scheduling,
        SwarmSpec swarm,
        WorkerSpec worker,
        TimeoutsSpec timeouts,
        RetrySpec retry
) {
    public static EngineExecutionSpec fromDomain(ExecutionConfigContent executionConfig) {
        return new EngineExecutionSpec(executionConfig.type(), executionConfig.scheduling(), executionConfig.swarm(),
                executionConfig.worker(), executionConfig.timeouts(), executionConfig.retry());
    }
}
