package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.model.InputArtifact;
import ru.vlad2509.minionflow.domain.model.enums.InputType;

@Entity
@Table(name = "input_artifacts")
public class InputArtifactEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ArtifactEntity artifact;

    @Column(name = "alias", nullable = false)
    public String alias;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public InputType type;

    public InputArtifactEntity() {
    }

    public InputArtifactEntity(Long id, ArtifactEntity artifact, String alias, InputType type) {
        this.id = id;
        this.artifact = artifact;
        this.alias = alias;
        this.type = type;
    }

    public InputArtifact toDomain(){
        return new InputArtifact(artifact.id, artifact.projectId, artifact.userId, artifact.size, artifact.originalName,
                artifact.contentType, artifact.createdAt, artifact.type, artifact.storageIdentifier.toDomain(), id, alias, type);
    }

    public static InputArtifactEntity fromDomain(InputArtifact inputArtifact, ArtifactEntity artifact){
        return new InputArtifactEntity(inputArtifact.getInternalId(), artifact, inputArtifact.getAlias(), inputArtifact.getInputType());
    }
}
