package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.model.TaskStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "task_runs")
public class TaskRun extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(nullable = false)
    public UUID projectId;

    @Column(nullable = false)
    public UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TaskStatus status;

    @ManyToOne(optional = true)
    @JoinColumn(name = "jar_artifact_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public StorageIdentifier jarArtifact;

    @ManyToOne(optional = true)
    @JoinColumn(name = "input_artifact_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public StorageIdentifier inputArtifact;

    @ManyToOne(optional = true)
    @JoinColumn(name = "jar_jpa_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public JarArtifact jarJpa;

    @ManyToOne(optional = true)
    @JoinColumn(name = "input_jpa_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public InputArtifact inputJpa;

    @ManyToOne(optional = true)
    @JoinColumn(name = "execution_config_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public ExecutionConfigJpa executionConfig;

    @ManyToMany
    @JoinTable(
            name = "task-run_artifact",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "output_id")
    )
    public Set<Artifact> outputs = new HashSet<Artifact>();

    @Column(nullable = false)
    public Instant createdAt;

    @Column(nullable = true)
    public Instant startedAt;

    @Column(nullable = true)
    public Instant finishedAt;

    @Column(nullable = true)
    public Instant doneAt;

    public TaskRun() {
    }

    public TaskRun(UUID projectId, UUID userId, StorageIdentifier jarArtifact, StorageIdentifier inputArtifact,
                   JarArtifact jarJpa, InputArtifact inputJpa, ExecutionConfigJpa executionConfig) {

        this.projectId = projectId;
        this.userId = userId;
        this.jarArtifact = jarArtifact;
        this.inputArtifact = inputArtifact;
        this.jarJpa = jarJpa;
        this.inputJpa = inputJpa;
        this.executionConfig = executionConfig;

        this.id = UUID.randomUUID();
        this.status = TaskStatus.CREATED;
        this.createdAt = Instant.now();
        this.startedAt = null;
        this.finishedAt = null;
        this.doneAt = null;
    }
}
