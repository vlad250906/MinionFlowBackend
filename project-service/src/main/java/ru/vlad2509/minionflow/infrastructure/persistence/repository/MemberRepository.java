package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Member;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MemberRepository implements PanacheRepository<Member> {

    public Optional<Member> findByProjectUserId(UUID projectId, UUID userId) {
        return find("project.id = ?1 and userId = ?2", projectId, userId).singleResultOptional();
    }

    public List<Project> findAllProjects(PaginationContext context, UUID userId){
        var query = find("userId", userId);
        query.page(Page.of(context.getPageIndex(), context.getPageSize()));
        context.acceptResult((int) query.count(), query.pageCount()); // будет забавно, если у чела более 2 миллиардов проектов
        return query.stream().map(member -> member.project).toList();
    }

    public List<Member> findAllMembers(PaginationContext context, UUID projectId){
        var query = find("project.id", projectId);
        query.page(Page.of(context.getPageIndex(), context.getPageSize()));
        context.acceptResult((int) query.count(), query.pageCount()); // 25% всего населения мира участвуют в проекте...
        return query.list();
    }

    public long deleteByProjectUser(UUID projectId, UUID userId) {
        return delete("project.id = ?1 and userId = ?2", projectId, userId);
    }

}
