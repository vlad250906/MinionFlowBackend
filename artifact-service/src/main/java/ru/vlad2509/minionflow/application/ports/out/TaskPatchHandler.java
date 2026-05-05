package ru.vlad2509.minionflow.application.ports.out;

import ru.vlad2509.minionflow.application.dto.engine.EngineTaskStatus;

import java.util.UUID;

public interface TaskPatchHandler {

    void updateTaskStatus(UUID taskId, EngineTaskStatus engineTaskStatus);
    void outputReady(UUID taskId);
}
