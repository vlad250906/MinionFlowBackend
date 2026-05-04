package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.model.InputArtifact;
import ru.vlad2509.minionflow.domain.model.JarArtifact;

@Entity
@Table(name = "jar_artifacts")
public class JarArtifactEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ArtifactEntity artifact;

    @Column(name = "alias", nullable = false)
    public String alias;

    public JarArtifactEntity() {
    }

    public JarArtifactEntity(Long id, ArtifactEntity artifact, String alias) {
        this.id = id;
        this.artifact = artifact;
        this.alias = alias;
    }

    public JarArtifact toDomain(){
        return new JarArtifact(artifact.id, artifact.projectId, artifact.userId, artifact.size, artifact.originalName,
                artifact.contentType, artifact.createdAt, artifact.type, artifact.storageIdentifier.toDomain(), id, alias);
    }

    public static JarArtifactEntity fromDomain(JarArtifact jarArtifact, ArtifactEntity artifact){
        return new JarArtifactEntity(jarArtifact.getInternalId(), artifact, jarArtifact.getAlias());
    }
}
