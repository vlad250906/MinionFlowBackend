package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.light.ExecutionConfigLight;
import ru.vlad2509.minionflow.application.dto.light.TaskRunLight;
import ru.vlad2509.minionflow.domain.model.ExecutionConfig;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ExecutionConfigEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ExecutionConfigRepository implements PanacheRepository<ExecutionConfigEntity> {

    @Inject
    EntityManager em;

    public Optional<ExecutionConfig> findById(UUID id) {
        return find("id", id).singleResultOptional().map(ExecutionConfigEntity::toDomain);
    }

    public List<ExecutionConfigLight> findAllProjectConfigs(PaginationContext context, UUID projectId) {
        List<ExecutionConfigLight> res = em.createQuery("""
                        select new ru.vlad2509.minionflow.application.dto.light.ExecutionConfigLight(
                            e.id,
                            e.alias,
                            e.userId,
                            e.createdAt
                        )
                        from ExecutionConfigEntity e
                        where e.projectId = :projectId
                        order by e.createdAt desc, e.id desc
                        """, ExecutionConfigLight.class)
                .setParameter("projectId", projectId)
                .setFirstResult(context.getPageIndex() * context.getPageSize())
                .setMaxResults(context.getPageSize())
                .getResultList();

        long total = em.createQuery("""
                        select count(e)
                        from ExecutionConfigEntity e
                        where e.projectId = :projectId
                        """, Long.class)
                .setParameter("projectId", projectId)
                .getSingleResult();

        context.acceptResult((int) total, Math.ceilDiv((int) total, context.getPageSize()));
        return res;
    }

    @Transactional
    public long deleteById(UUID id) {
        return delete("id", id);
    }

    @Transactional
    public void create(ExecutionConfig executionConfig) {
        this.persist(ExecutionConfigEntity.fromDomain(executionConfig));
    }

    @Transactional
    public void update(ExecutionConfig executionConfig) {
        this.update("alias = ?1, content = ?2 where id = ?3", executionConfig.getAlias(), executionConfig.getContent(), executionConfig.getId());
    }
}
