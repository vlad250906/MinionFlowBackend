package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.Member;
import ru.vlad2509.minionflow.domain.enums.MemberRole;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "project_members",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "projectmembers_project_user_uniq",
                columnNames = {"project_id", "user_id"}
        )
})

public class MemberEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ProjectEntity project;

    @Column(nullable = false, name = "user_id")
    public UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    public RemoteUser remoteUser;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public MemberRole role;

    @Column(nullable = false)
    public Instant memberSince;

    public MemberEntity(Long id, ProjectEntity projectEntity, UUID userId, RemoteUser remoteUser, MemberRole role, Instant memberSince) {
        this.id = id;
        this.project = projectEntity;
        this.userId = userId;
        this.remoteUser = remoteUser;
        this.role = role;
        this.memberSince = memberSince;
    }

    public MemberEntity() {
    }

    public Member toDomain(){
        return new Member(id, project.toDomain(), userId, remoteUser == null ? null : remoteUser.username, role, memberSince);
    }


    public static MemberEntity fromDomain(Member member, ProjectEntity projectEntity){
        return new MemberEntity(member.getInternalId(),
                projectEntity, member.getUserId(),
                null,
                member.getRole(), member.getMemberSince());
    }
}
