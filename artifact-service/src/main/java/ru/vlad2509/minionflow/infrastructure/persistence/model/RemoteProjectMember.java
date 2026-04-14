package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import ru.vlad2509.minionflow.domain.model.MemberRole;

import java.util.UUID;

@Entity
@Table(name = "remote_project_members")
public class RemoteProjectMember extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public UUID projectId;

    @Column(nullable = false)
    public UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public MemberRole role;

    public RemoteProjectMember() {
    }

    public RemoteProjectMember(UUID projectId, UUID userId, MemberRole role) {
        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
    }
}
