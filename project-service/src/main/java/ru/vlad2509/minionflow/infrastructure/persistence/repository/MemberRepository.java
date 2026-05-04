package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.domain.Member;
import ru.vlad2509.minionflow.domain.Project;
import ru.vlad2509.minionflow.infrastructure.persistence.model.MemberEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ProjectEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MemberRepository implements PanacheRepository<MemberEntity> {

    @Inject
    EntityManager em;

    public Optional<Member> findByProjectUserId(UUID projectId, UUID userId) {
        return find("project.id = ?1 and userId = ?2", projectId, userId).singleResultOptional().map(MemberEntity::toDomain);
    }

    public List<Project> findAllProjects(PaginationContext context, UUID userId){
        var query = find("userId", userId);
        query.page(Page.of(context.getPageIndex(), context.getPageSize()));
        context.acceptResult((int) query.count(), query.pageCount()); // будет забавно, если у чела более 2 миллиардов проектов
        return query.stream().map(memberEntity -> memberEntity.project.toDomain()).toList();
    }

    public List<Member> findAllMembers(PaginationContext context, UUID projectId){
        var query = find("project.id", projectId);
        query.page(Page.of(context.getPageIndex(), context.getPageSize()));
        context.acceptResult((int) query.count(), query.pageCount()); // 25% всего населения мира участвуют в проекте...
        return query.list().stream().map(MemberEntity::toDomain).toList();
    }

    @Transactional
    public void updateRole(Member member){
        this.update("role = ?1 where id = ?2", member.getRole(), member.getInternalId());
    }

    @Transactional
    public void create(Member member) {
        ProjectEntity projectRef = em.getReference(ProjectEntity.class, member.getProject().getId());
        MemberEntity entity = MemberEntity.fromDomain(member, projectRef);
        this.persist(entity);
    }

    @Transactional
    public long deleteByProjectUser(UUID projectId, UUID userId) {
        return delete("project.id = ?1 and userId = ?2", projectId, userId);
    }

}
