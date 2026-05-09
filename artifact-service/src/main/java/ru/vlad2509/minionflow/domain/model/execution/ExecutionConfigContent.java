package ru.vlad2509.minionflow.domain.model.execution;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.vlad2509.minionflow.domain.model.execution.network.NetworkSpec;
import ru.vlad2509.minionflow.domain.model.execution.retry.RetrySpec;
import ru.vlad2509.minionflow.domain.model.execution.scheduling.SchedulingSpec;
import ru.vlad2509.minionflow.domain.model.execution.swarm.SwarmSpec;
import ru.vlad2509.minionflow.domain.model.execution.worker.WorkerSpec;

public record ExecutionConfigContent(
        @NotNull ExecutionType type,
        @Valid NetworkSpec network,
        @Valid @NotNull SchedulingSpec scheduling,
        @Valid SwarmSpec swarm,
        @Valid @NotNull WorkerSpec worker,
        @Valid @NotNull TimeoutsSpec timeouts,
        @Valid @NotNull RetrySpec retry
) {

}