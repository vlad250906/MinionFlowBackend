package ru.vlad2509.minionflow.infrastructure.engine;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLog;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmAgent;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.ports.out.TaskEngine;
import ru.vlad2509.minionflow.application.ports.out.TaskPatchHandler;
import ru.vlad2509.minionflow.domain.model.TaskRun;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Named("MinionFlowTaskEngine")
@ApplicationScoped
public class MinionFlowEngine implements TaskEngine {

    private TaskPatchHandler taskPatchHandler;

    @Override
    public void startTask(TaskRun taskRun) {
        // TODO
    }

    @Override
    public void cancelTask(TaskRun taskRun) {
        // not supported
    }

    @Override
    public Optional<UUID> getTaskByMicrotaskId(UUID microtaskId) {
        // TODO
        return Optional.empty();
    }

    @Override
    public List<MicrotaskLog> getMicrotaskLogs(UUID microtaskId, int afterSeq, int limit) {
        // TODO
        return List.of();
    }

    @Override
    public StatelessTaskState getStatelessState(TaskRun taskRun) {
        // TODO
        return null;
    }

    @Override
    public StatelessMicrotaskRun getStatelessMicrotask(TaskRun taskRun, UUID microtaskId) {
        // TODO
        return null;
    }

    @Override
    public SwarmTaskState getSwarmState(TaskRun taskRun) {
        // TODO
        return null;
    }

    @Override
    public SwarmMicrotaskRun getSwarmMicrotask(TaskRun taskRun, UUID microtaskId) {
        // TODO
        return null;
    }

    @Override
    public SwarmAgent getSwarmAgent(TaskRun taskRun, UUID agentId) {
        // TODO
        return null;
    }

    @Override
    public void registerPatchHandler(TaskPatchHandler handler) {
        this.taskPatchHandler = handler;
    }
}
