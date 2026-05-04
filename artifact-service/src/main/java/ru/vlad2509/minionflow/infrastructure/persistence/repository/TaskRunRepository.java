package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.light.TaskRunLight;
import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.TaskRun;
import ru.vlad2509.minionflow.infrastructure.persistence.model.*;

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

    //TODO: метод не перетаскивает output!
    @Transactional
    public void create(TaskRun taskRun){
        // FIME: больше не пытаться делать чистую архитекутуру на проектах, в которых дофига репозиториев
        StorageIdentifierEntity jarArtifact = em.find(StorageIdentifierEntity.class, taskRun.getJarArtifactIdentifier().getInternalId());
        StorageIdentifierEntity inputArtifact = em.find(StorageIdentifierEntity.class, taskRun.getInputArtifactIdentifier().getInternalId());
        JarArtifactEntity jarJpa = em.find(JarArtifactEntity.class, taskRun.getJarArtifact().getInternalId());
        InputArtifactEntity inputJpa = em.find(InputArtifactEntity.class, taskRun.getInputArtifact().getInternalId());
        ExecutionConfigEntity executionConfig = em.find(ExecutionConfigEntity.class, taskRun.getExecutionConfig().getId());
        em.persist(TaskRunEntity.fromDomain(taskRun, jarArtifact, inputArtifact, jarJpa, inputJpa, executionConfig, new HashSet<>()));
    }

    @Transactional
    public void updateOutputs(TaskRun taskRun) {
        Optional<TaskRunEntity> taskRunOptional = find("id", taskRun.getId()).singleResultOptional();
        if (taskRunOptional.isEmpty())
            return;
        for (ArtifactEntity artifactEntity : artifactRepository.find("id in ?1", taskRun.getOutputs().stream().map(Artifact::getId)).list()) {
            taskRunOptional.get().outputs.add(artifactEntity);
        }
    }

}
