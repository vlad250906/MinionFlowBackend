package ru.vlad2509.minionflow.application.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.ports.out.S3Service;
import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.StorageIdentifier;
import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ArtifactEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.ArtifactRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ArtifactService {

    @Inject
    ArtifactRepository artifactRepository;

    @Inject
    S3Service s3Service;

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Artifact createArtifact(UserContext userContext, String storageKeyPrefix,
                                   UUID projectId, FileUpload file, ArtifactType type) {
        String storageKey = storageKeyPrefix + "/" + UUID.randomUUID();
        Artifact artifact = new Artifact(projectId, userContext.userId(), file.size(),
                file.fileName(), file.contentType(), type, new StorageIdentifier(storageKey));
        artifactRepository.create(artifact);

        if (!s3Service.upload(storageKey, file)) {
            artifactRepository.delete(artifact.getId());
            throw new ApiException(ApiError.S3_UNAVAILABLE);
        }

        return artifact;
    }

    public List<Artifact> discoverArtifact(String keyPrefix, UUID userId, UUID projectId) {
        List<S3Service.S3Object> artifacts = s3Service.enumerateFiles(keyPrefix);
        return discoverArtifactsTransactional(userId, projectId, artifacts);
    }

    public Artifact getArtifactMetadata(UserContext userContext, UUID artifactId) {
        return artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
    }

    public Artifact updateArtifactContent(UserContext userContext, String storageKeyPrefix,
                                          UUID projectId, UUID artifactId, FileUpload file) {
        String storageKey = storageKeyPrefix + "/" + UUID.randomUUID();

        if (!s3Service.upload(storageKey, file))
            throw new ApiException(ApiError.S3_UNAVAILABLE);

        return updateArtifactTransactional(artifactId, file, storageKey);
    }

    public StreamingOutput downloadArtifact(UserContext userContext, UUID projectId, UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        if (!projectId.equals(artifact.getProjectId()))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "exists, but in different project");
        return s3Service.download(artifact.getStorageIdentifier().getStorageKey());
    }

    @Transactional
    public void deleteArtifact(UserContext userContext, UUID artifactId) {
        if (artifactRepository.delete(artifactId) <= 0)
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND);
    }

    @Transactional
    public List<Artifact> discoverArtifactsTransactional(UUID userId, UUID projectId, List<S3Service.S3Object> artifacts) {
        List<Artifact> result = new ArrayList<>();
        for (S3Service.S3Object object : artifacts) {
            String[] chunks = object.key().split("/");
            String filename = chunks[chunks.length - 1];
            Artifact artifact = new Artifact(projectId, userId, object.size(), filename, MediaType.APPLICATION_OCTET_STREAM, ArtifactType.OUTPUT, new StorageIdentifier(object.key()));
            artifactRepository.create(artifact);
            result.add(artifact);
        }

        return result;
    }

    @Transactional
    public Artifact updateArtifactTransactional(UUID artifactId, FileUpload file, String storageKey) {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        artifact.update(file.contentType(), file.fileName(), file.size(), new StorageIdentifier(storageKey));
        artifactRepository.updateContentMeta(artifact);
        return artifact;
    }


}
