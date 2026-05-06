package ru.vlad2509.minionflow.domain.model;

import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionType;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TaskRun {

    private UUID id;
    private UUID projectId;
    private UUID userId;
    private TaskStatus status;
    private StorageIdentifier jarArtifactIdentifier;
    private StorageIdentifier inputArtifactIdentifier;
    private JarArtifact jarArtifact;
    private InputArtifact inputArtifact;
    private ExecutionConfig executionConfig;
    private ExecutionType taskType;
    private Set<Artifact> outputs = new HashSet<Artifact>();
    private Instant createdAt;
    private Instant startedAt;
    private Instant finishedAt;
    private Instant doneAt;

    // жесть конструктор :\
    public TaskRun(UUID id, UUID projectId, UUID userId, TaskStatus status, StorageIdentifier jarArtifactIdentifier,
                   StorageIdentifier inputArtifactIdentifier, JarArtifact jarArtifact, InputArtifact inputArtifact,
                   ExecutionConfig executionConfig, ExecutionType taskType, Set<Artifact> outputs, Instant createdAt, Instant startedAt,
                   Instant finishedAt, Instant doneAt) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.status = status;
        this.jarArtifactIdentifier = jarArtifactIdentifier;
        this.inputArtifactIdentifier = inputArtifactIdentifier;
        this.jarArtifact = jarArtifact;
        this.inputArtifact = inputArtifact;
        this.executionConfig = executionConfig;
        this.taskType = taskType;
        this.outputs = outputs;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.doneAt = doneAt;
    }

    public TaskRun(UUID projectId, UUID userId, StorageIdentifier jarArtifactIdentifier,
                   StorageIdentifier inputArtifactIdentifier, JarArtifact jarArtifact, InputArtifact inputArtifact,
                   ExecutionConfig executionConfig) {
        this.projectId = projectId;
        this.userId = userId;
        this.jarArtifactIdentifier = jarArtifactIdentifier;
        this.inputArtifactIdentifier = inputArtifactIdentifier;
        this.jarArtifact = jarArtifact;
        this.inputArtifact = inputArtifact;
        this.executionConfig = executionConfig;

        this.id = UUID.randomUUID();
        this.status = TaskStatus.CREATED;
        this.createdAt = Instant.now();
        this.taskType = executionConfig.getContent().type();
        this.startedAt = null;
        this.finishedAt = null;
        this.doneAt = null;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public StorageIdentifier getJarArtifactIdentifier() {
        return jarArtifactIdentifier;
    }

    public StorageIdentifier getInputArtifactIdentifier() {
        return inputArtifactIdentifier;
    }

    public JarArtifact getJarArtifact() {
        return jarArtifact;
    }

    public InputArtifact getInputArtifact() {
        return inputArtifact;
    }

    public ExecutionConfig getExecutionConfig() {
        return executionConfig;
    }

    public ExecutionType getTaskType() {
        return taskType;
    }

    public Set<Artifact> getOutputs() {
        return outputs;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public Instant getDoneAt() {
        return doneAt;
    }

    public boolean setStatus(TaskStatus status) {
        if(!this.status.canChangeTo(status))
            return false;

        this.status = status;
        return true;
    }

    public void addOutputs(List<Artifact> outputs) {
        this.outputs.addAll(outputs);
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public void setDoneAt(Instant doneAt) {
        this.doneAt = doneAt;
    }
}
