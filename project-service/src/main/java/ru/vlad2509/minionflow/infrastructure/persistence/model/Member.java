package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.MemberRole;

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

public class Member extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Project project;

    @Column(nullable = false, name = "user_id")
    public UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public MemberRole role;

    @Column(nullable = false)
    public Instant memberSince;

    public Member(Project project, UUID userId, MemberRole role) {
        this.project = project;
        this.userId = userId;
        this.role = role;
        this.memberSince = Instant.now();
    }

    public Member() {
    }
}
