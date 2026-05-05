package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.domain.model.enums.TaskStatus;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "task_runs")
public class TaskRunEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "project_id", nullable = false)
    public UUID projectId;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public TaskStatus status;

    // тут хранятся и identifier-ы, и сами сущности артефактов (т.к. кто-то мог обновить сущность "входные данные"
    //                      новым содержимым, поэтому храним и сущность, и конкретный файл, с которым был запуск)
    // + хранение identifier гарантирует, что он не будет удалён из s3, пока есть хоть одна таска, запущенная на нём

    @ManyToOne(optional = true)
    @JoinColumn(name = "jar_artifact_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public StorageIdentifierEntity jarArtifact;

    @ManyToOne(optional = true)
    @JoinColumn(name = "input_artifact_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public StorageIdentifierEntity inputArtifact;

    @ManyToOne(optional = true)
    @JoinColumn(name = "jar_jpa_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public JarArtifactEntity jarJpa;

    @ManyToOne(optional = true)
    @JoinColumn(name = "input_jpa_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public InputArtifactEntity inputJpa;

    @ManyToOne(optional = true)
    @JoinColumn(name = "execution_config_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public ExecutionConfigEntity executionConfig;

    @ManyToMany
    @JoinTable(
            name = "task-run_artifact",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "output_id")
    )
    public Set<ArtifactEntity> outputs = new HashSet<ArtifactEntity>();

    // дублируем на случай, если кто-то решит обновить конфиг во время работы таски
    @Column(name = "task_type", nullable = false)
    public ExecutionType taskType;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    public Instant createdAt;

    @Column(name = "started_at", nullable = true, columnDefinition = "timestamptz")
    public Instant startedAt;

    @Column(name = "finished_at", nullable = true, columnDefinition = "timestamptz")
    public Instant finishedAt;

    @Column(name = "done_at", nullable = true, columnDefinition = "timestamptz")
    public Instant doneAt;

    @Column(name = "output_ready", nullable = false)
    public boolean outputReady;

    public TaskRunEntity() {
    }

    // 16...
    public TaskRunEntity(UUID id, UUID projectId, UUID userId, TaskStatus status, StorageIdentifierEntity jarArtifact,
                         StorageIdentifierEntity inputArtifact, JarArtifactEntity jarJpa, InputArtifactEntity inputJpa,
                         ExecutionConfigEntity executionConfig, Set<ArtifactEntity> outputs, ExecutionType taskType,
                         Instant createdAt, Instant startedAt, Instant finishedAt, Instant doneAt, boolean outputReady) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.status = status;
        this.jarArtifact = jarArtifact;
        this.inputArtifact = inputArtifact;
        this.jarJpa = jarJpa;
        this.inputJpa = inputJpa;
        this.executionConfig = executionConfig;
        this.outputs = outputs;
        this.taskType = taskType;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.doneAt = doneAt;
        this.outputReady = outputReady;
    }

    public TaskRun toDomain(){
        return new TaskRun(id, projectId, userId, status, jarArtifact.toDomain(), inputArtifact.toDomain(),
                jarJpa.toDomain(), inputJpa.toDomain(), executionConfig.toDomain(),
                taskType, outputs.stream().map(ArtifactEntity::toDomain).collect(Collectors.toSet()), createdAt,
                startedAt, finishedAt, doneAt, outputReady);
    }

    public static TaskRunEntity fromDomain(TaskRun taskRun, StorageIdentifierEntity jarArtifact,
                                           StorageIdentifierEntity inputArtifact, JarArtifactEntity jarJpa,
                                           InputArtifactEntity inputJpa, ExecutionConfigEntity executionConfig,
                                           Set<ArtifactEntity> outputs) {
        return new TaskRunEntity(taskRun.getId(), taskRun.getProjectId(), taskRun.getUserId(), taskRun.getStatus(),
                jarArtifact, inputArtifact, jarJpa, inputJpa, executionConfig, outputs, taskRun.getTaskType(),
                taskRun.getCreatedAt(), taskRun.getStartedAt(), taskRun.getFinishedAt(), taskRun.getDoneAt(), taskRun.isOutputReady());
    }
}
