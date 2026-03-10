package ru.vlad2509.minionflow.infrastructure.persistence.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vlad2509.minionflow.domain.model.InputType;

@Entity
@Table(name = "jar_artifacts")
public class JarArtifact extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Artifact artifact;

    @Column(nullable = false)
    public String alias;

    public JarArtifact() {
    }

    public JarArtifact(Artifact artifact, String alias) {
        this.artifact = artifact;
        this.alias = alias;
    }
}
