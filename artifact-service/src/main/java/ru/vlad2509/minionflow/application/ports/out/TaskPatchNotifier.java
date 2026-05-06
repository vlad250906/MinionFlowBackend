package ru.vlad2509.minionflow.application.ports.out;

import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;

public interface TaskPatchNotifier {

    void sendStatelessStatePatch(StatelessTaskState state);
    void sendSwarmStatePatch(SwarmTaskState state);
    void sendLogBatch(MicrotaskLogsBatch batch);
}
