package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
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

    public List<Project> findAllProjects(UUID userId){
        return find("userId", userId).stream().map(member -> member.project).toList();
    }

    public List<Member> findAllMembers(UUID projectId){
        return find("project.id", projectId).list();
    }

    public long deleteByProjectUser(UUID projectId, UUID userId) {
        return delete("project.id = ?1 and userId = ?2", projectId, userId);
    }

}
