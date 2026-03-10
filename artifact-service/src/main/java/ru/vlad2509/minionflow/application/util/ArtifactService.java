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
import ru.vlad2509.minionflow.domain.model.ArtifactType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Artifact;
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

    public ArtifactDto createArtifact(UserContext userContext, String storageKeyPrefix,
                                      UUID projectId, FileUpload file, ArtifactType type) {
        String storageKey = storageKeyPrefix + "/" + UUID.randomUUID();
        ArtifactDto dto = createArtifactTransactional(userContext, projectId, file, type, storageKey);

        if (!s3Service.upload(storageKey, file)) {
            artifactRepository.delete(dto.artifactId());
            throw new ApiException(ApiError.S3_UNAVAILABLE);
        }

        return dto;
    }

    public List<ArtifactDto> discoverArtifact(String keyPrefix, UUID userId, UUID projectId) {
        List<S3Service.S3Object> artifacts = s3Service.enumerateFiles(keyPrefix);
        return discoverArtifactsTransactional(userId, projectId, artifacts);
    }

    @Transactional
    public void deleteArtifact(UserContext userContext, UUID artifactId) {
        if (artifactRepository.delete(artifactId) <= 0)
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND);
    }

    public ArtifactDto getArtifactMetadata(UserContext userContext, UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        return ArtifactDto.fromJpa(artifact);
    }

    public List<ArtifactDto> getArtifacts(UserContext userContext, PaginationContext paginationContext, UUID projectId,
                                          ArtifactType type) {
        return artifactRepository.findAllProjectArtifacts(paginationContext, projectId, type).stream()
                .map(ArtifactDto::fromJpa).toList();
    }


    public ArtifactDto updateArtifactContent(UserContext userContext, String storageKeyPrefix,
                                             UUID projectId, UUID artifactId, FileUpload file) {
        String storageKey = storageKeyPrefix + "/" + UUID.randomUUID();

        if (!s3Service.upload(storageKey, file))
            throw new ApiException(ApiError.S3_UNAVAILABLE);

        return updateArtifactTransactional(artifactId, userContext, projectId, file, storageKey);
    }

    public StreamingOutput downloadArtifact(UserContext userContext, UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        return s3Service.download(artifact.getStorageKey());
    }

    @Transactional
    public ArtifactDto createArtifactTransactional(UserContext userContext, UUID projectId,
                                                   FileUpload file, ArtifactType type, String storageKey) {
        Artifact artifact = artifactRepository.create(projectId, userContext.userId(), type, file.size(),
                file.fileName(), file.contentType(), storageKey);
        return ArtifactDto.fromJpa(artifact);
    }

    @Transactional
    public List<ArtifactDto> discoverArtifactsTransactional(UUID userId, UUID projectId, List<S3Service.S3Object> artifacts) {
        List<ArtifactDto> result = new ArrayList<>();
        for (S3Service.S3Object object : artifacts) {
            String[] chunks = object.key().split("/");
            String filename = chunks[chunks.length - 1];
            Artifact artifact = artifactRepository.create(projectId, userId, ArtifactType.OUTPUT, object.size(), filename, MediaType.APPLICATION_OCTET_STREAM, object.key());
            result.add(ArtifactDto.fromJpa(artifact));
        }

        return result;
    }

    @Transactional
    public ArtifactDto updateArtifactTransactional(UUID artifactId, UserContext userContext, UUID projectId,
                                                   FileUpload file, String storageKey) {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        artifact.contentType = file.contentType();
        artifact.originalName = file.fileName();
        artifact.size = file.size();

        return ArtifactDto.fromJpa(artifact);
    }


}
