package ru.vlad2509.minionflow.domain.model.execution;

import jakarta.validation.Valid;
import ru.vlad2509.minionflow.domain.model.execution.network.NetworkSpec;
import ru.vlad2509.minionflow.domain.model.execution.retry.RetrySpec;
import ru.vlad2509.minionflow.domain.model.execution.scheduling.SchedulingSpec;
import ru.vlad2509.minionflow.domain.model.execution.swarm.SwarmSpec;
import ru.vlad2509.minionflow.domain.model.execution.worker.WorkerSpec;

public record ExecutionConfigContent(
        ExecutionType type,
        @Valid NetworkSpec network,
        @Valid SchedulingSpec scheduling,
        @Valid SwarmSpec swarm,
        @Valid WorkerSpec worker,
        @Valid TimeoutsSpec timeouts,
        @Valid RetrySpec retry
) {

}