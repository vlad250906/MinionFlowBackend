package ru.vlad2509.minionflow.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.domain.model.enums.MemberRole;
import ru.vlad2509.minionflow.infrastructure.persistence.model.RemoteProjectMember;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RemoteProjectMemberRepository implements PanacheRepository<RemoteProjectMember> {

    @Transactional
    public Optional<RemoteProjectMember> findByProjectUserId(UUID projectId, UUID userId) {
        return find("projectId = ?1 and userId = ?2", projectId, userId).singleResultOptional();
    }

    @Transactional
    public void updateOrCreate(UUID projectId, UUID userId, MemberRole role) {
        Optional<RemoteProjectMember> memberOptional = this.find("projectId = ?1 and userId = ?2", projectId, userId).singleResultOptional();
        if (memberOptional.isEmpty()) {
            this.persist(new RemoteProjectMember(projectId, userId, role));
            return;
        }

        RemoteProjectMember member = memberOptional.get();
        member.role = role;
    }

    @Transactional
    public void delete(UUID projectId, UUID userId) {
        this.delete("projectId = ?1 and userId = ?2", projectId, userId);
    }

}
