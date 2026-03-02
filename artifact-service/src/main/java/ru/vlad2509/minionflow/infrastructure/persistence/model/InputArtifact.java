package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.InputType;

@Entity
@Table(name = "input_artifacts")
public class InputArtifact extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Artifact artifact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public InputType type;

    public InputArtifact() {
    }

    public InputArtifact(Artifact artifact, InputType type) {
        this.artifact = artifact;
        this.type = type;
    }


}
