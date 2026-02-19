package ru.vlad2509.minionflow.application;

import com.google.protobuf.Api;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.ports.out.S3Service;
import ru.vlad2509.minionflow.domain.ArtifactType;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Artifact;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.ArtifactRepository;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ArtifactService {

    @Inject
    ArtifactRepository artifactRepository;

    @Inject
    S3Service s3Service;

    public ArtifactDto createArtifact(UserContext userContext, UUID projectId, String alias, FileUpload file, ArtifactType type) {
        String hashAlgorithm = "SHA-256";
        String hashValue = sha256Sum(file);
        String storageKey = "minionflow/artifacts/" + projectId + "/" + UUID.randomUUID() + "_" + file.fileName();

        ArtifactDto dto = createArtifactTransactional(userContext, projectId, alias, file, type, hashAlgorithm, hashValue, storageKey);

        if (!s3Service.upload(storageKey, file)) {
            artifactRepository.hardDelete(dto.artifactId());
            throw new ApiException(ApiError.S3_UNAVAILABLE);
        }

        return dto;
    }

    @Transactional
    public ArtifactDto updateArtifactMetadata(UserContext userContext, UUID artifactId, String alias) {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        artifact.alias = alias;
        return ArtifactDto.fromJpa(artifact);
    }

    @Transactional
    public void deleteArtifact(UserContext userContext, UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        artifact.markDeleted = true;
    }

    public ArtifactDto getArtifactMetadata(UserContext userContext, UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        return ArtifactDto.fromJpa(artifact);
    }

    public List<ArtifactDto> getArtifacts(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        return artifactRepository.findAllProjectArtifacts(paginationContext, projectId).stream().map(ArtifactDto::fromJpa).toList();
    }


    public ArtifactDto updateArtifactContent(UserContext userContext, UUID projectId, UUID artifactId, FileUpload file) {
        String hashAlgorithm = "SHA-256";
        String hashValue = sha256Sum(file);
        String storageKey = "minionflow/artifacts/" + projectId + "/" + UUID.randomUUID() + "_" + file.fileName();

        if (!s3Service.upload(storageKey, file))
            throw new ApiException(ApiError.S3_UNAVAILABLE);

        ArtifactDto dto = updateArtifactTransactional(artifactId, userContext, projectId, file, hashAlgorithm, hashValue, storageKey);
        return dto;
    }

    public StreamingOutput downloadArtifact(UserContext userContext, UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        StreamingOutput streamingOutput = s3Service.download(artifact.storageKey);
        return streamingOutput;
    }

    @Transactional
    public ArtifactDto createArtifactTransactional(UserContext userContext, UUID projectId, String alias,
                                                    FileUpload file, ArtifactType type, String hashAlg, String hashVal,
                                                    String storageKey) {
        Artifact artifact = new Artifact(projectId, userContext.userId(), type, alias, file.size(),
                file.fileName(), file.contentType(), hashAlg, hashVal, storageKey);
        artifactRepository.persist(artifact);
        return ArtifactDto.fromJpa(artifact);
    }

    @Transactional
    public ArtifactDto updateArtifactTransactional(UUID artifactId, UserContext userContext, UUID projectId,
                                                    FileUpload file, String hashAlg, String hashVal,
                                                    String storageKey) {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND));
        artifact.contentType = file.contentType();
        artifact.hashAlgorithm = hashAlg;
        artifact.hashValue = hashVal;
        artifact.originalName = file.fileName();
        artifact.size = file.size();
        artifact.storageKey = storageKey;

        return ArtifactDto.fromJpa(artifact);
    }

    private String sha256Sum(FileUpload file) {
        try (InputStream in = Files.newInputStream(file.uploadedFile());) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            DigestInputStream dis = new DigestInputStream(in, md);
            dis.transferTo(OutputStream.nullOutputStream());
            return HexFormat.of().formatHex(md.digest());
        } catch (Exception e) {
            throw new ApiException(ApiError.UNEXPECTED_ERROR);
        }
    }


}
