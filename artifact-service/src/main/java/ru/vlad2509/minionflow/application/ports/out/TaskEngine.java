package ru.vlad2509.minionflow.application.ports.out;

import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLog;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmAgent;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.domain.model.TaskRun;

import java.util.List;
import java.util.UUID;

public interface TaskEngine {

    void startTask(TaskRun taskRun);

    void cancelTask(TaskRun taskRun);

    List<MicrotaskLog> getMicrotaskLogs(UUID microtaskId, int afterSeq, int limit);

    StatelessTaskState getStatelessState(TaskRun taskRun);

    StatelessMicrotaskRun getStatelessMicrotask(TaskRun taskRun, UUID microtaskId);

    SwarmTaskState getSwarmState(TaskRun taskRun);

    SwarmMicrotaskRun getSwarmMicrotask(TaskRun taskRun, UUID microtaskId);

    SwarmAgent getSwarmAgent(TaskRun taskRun, UUID agentId);

    void registerPatchHandler(TaskPatchHandler handler);

}
