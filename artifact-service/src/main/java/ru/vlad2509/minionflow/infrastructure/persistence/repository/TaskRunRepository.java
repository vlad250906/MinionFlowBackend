package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.light.TaskRunLight;
import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.infrastructure.persistence.model.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TaskRunRepository implements PanacheRepository<TaskRunEntity> {

    @Inject
    EntityManager em;

    @Inject
    ArtifactRepository artifactRepository;

    // TODO: щас этот метод подтягивает кучу связей из БД. Особенно: парсит executionConfig (весь!). Надо либо как-то грузить лениво, или чаще юзать light версию (особенно при авторизации)
    @Transactional
    public Optional<TaskRun> findById(UUID id) {
        return find("id", id).singleResultOptional().map(TaskRunEntity::toDomain);
    }

    public List<TaskRunLight> findAllTasksLight(PaginationContext context, UUID projectId) {
        List<TaskRunLight> res = em.createQuery("""
                        select new ru.vlad2509.minionflow.application.dto.light.TaskRunLight(
                            t.id,
                            t.projectId,
                            t.userId,
                            t.status,
                            t.createdAt,
                            t.doneAt
                        )
                        from TaskRunEntity t
                        where t.projectId = :projectId
                        order by t.createdAt desc, t.id desc
                        """, TaskRunLight.class)
                .setParameter("projectId", projectId)
                .setFirstResult(context.getPageIndex() * context.getPageSize())
                .setMaxResults(context.getPageSize())
                .getResultList();

        long total = em.createQuery("""
                        select count(t)
                        from TaskRunEntity t
                        where t.projectId = :projectId
                        """, Long.class)
                .setParameter("projectId", projectId)
                .getSingleResult();

        context.acceptResult((int) total, Math.ceilDiv((int) total, context.getPageSize()));
        return res;
    }

    //FIXME: метод не перетаскивает output!
    @Transactional
    public void create(TaskRun taskRun){
        // FIXME: больше не пытаться делать чистую архитекутуру на проектах, в которых дофига репозиториев
        StorageIdentifierEntity jarArtifact = em.find(StorageIdentifierEntity.class, taskRun.getJarArtifactIdentifier().getInternalId());
        StorageIdentifierEntity inputArtifact = em.find(StorageIdentifierEntity.class, taskRun.getInputArtifactIdentifier().getInternalId());
        JarArtifactEntity jarJpa = em.find(JarArtifactEntity.class, taskRun.getJarArtifact().getInternalId());
        InputArtifactEntity inputJpa = em.find(InputArtifactEntity.class, taskRun.getInputArtifact().getInternalId());
        ExecutionConfigEntity executionConfig = em.find(ExecutionConfigEntity.class, taskRun.getExecutionConfig().getId());
        em.persist(TaskRunEntity.fromDomain(taskRun, jarArtifact, inputArtifact, jarJpa, inputJpa, executionConfig, new HashSet<>()));
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean updateOutputsIfEmpty(TaskRun taskRun) {
        TaskRunEntity taskRunEntity = find("id", taskRun.getId())
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();

        if (taskRunEntity == null || (taskRunEntity.outputs != null && !taskRunEntity.outputs.isEmpty()))
            return false;

        if (taskRunEntity.outputs == null)
            taskRunEntity.outputs = new HashSet<>();
        taskRunEntity.outputs.addAll(artifactRepository.find("id in ?1", taskRun.getOutputs().stream().map(Artifact::getId).toList()).list());
        return true;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<TaskRun> lockById(UUID id){
        return find("id", id)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .singleResultOptional().map(TaskRunEntity::toDomain);
    }

    @Transactional
    public void updateStatus(TaskRun taskRun){
        this.update("status = ?1 where id = ?2", taskRun.getStatus(), taskRun.getId());
        switch(taskRun.getStatus()){
            case STARTING -> this.update("startedAt = ?1 where id = ?2", Instant.now(), taskRun.getId());
            case FINISHED -> this.update("finishedAt = ?1 where id = ?2", Instant.now(), taskRun.getId());
            case DONE, FAILED -> this.update("doneAt = ?1 where id = ?2", Instant.now(), taskRun.getId());
            default -> {}
        }
    }

}
