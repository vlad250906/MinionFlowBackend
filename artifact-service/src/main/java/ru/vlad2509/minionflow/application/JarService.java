package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.dto.JarArtifactDto;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.ArtifactService;
import ru.vlad2509.minionflow.application.util.StorageKeyFactory;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.InputArtifact;
import ru.vlad2509.minionflow.domain.model.JarArtifact;
import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;
import ru.vlad2509.minionflow.domain.model.enums.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.persistence.model.JarArtifactEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.JarArtifactRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class JarService {

    @Inject
    ArtifactService artifactService;

    @Inject
    TokenService tokenService;

    @Inject
    StorageKeyFactory storageKeyFactory;

    @Inject
    JarArtifactRepository jarArtifactRepository;

    public JarArtifactDto createJar(UserContext userContext, UUID projectId, String alias, FileUpload file) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        Artifact artifact = artifactService.createArtifact(userContext, storageKeyFactory.generateJarPrefix(projectId),
                projectId, file, ArtifactType.JAR);
        JarArtifact jarArtifact = new JarArtifact(artifact, alias);
        jarArtifactRepository.createJarArtifact(jarArtifact);
        return JarArtifactDto.fromDomain(jarArtifact);
    }

    public JarArtifactDto updateJarMetadata(UserContext userContext, UUID projectId, UUID artifactId, String alias) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        JarArtifact jarArtifact = jarArtifactRepository.findByArtifactId(artifactId)
                .orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "jar artifact not found"));
        if (!projectId.equals(jarArtifact.getProjectId()))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "exists, but in different project");
        jarArtifact.setAlias(alias);
        jarArtifactRepository.update(jarArtifact);
        return JarArtifactDto.fromDomain(jarArtifact);
    }

    public void deleteJar(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        if (!projectId.equals(jarArtifactRepository.findByArtifactId(artifactId).map(JarArtifact::getProjectId).orElse(null)))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "exists, but in different project");
        artifactService.deleteArtifact(userContext, artifactId);
    }

    public JarArtifactDto getJarMetadata(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_READ);
        JarArtifact jarArtifact = jarArtifactRepository.findByArtifactId(artifactId).orElseThrow(
                () -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "jar artifact not found"));
        if (!projectId.equals(jarArtifact.getProjectId()))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "exists, but in different project");
        return JarArtifactDto.fromDomain(jarArtifact);
    }

    public List<JarArtifactDto> getJars(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_READ);
        return jarArtifactRepository.findAllProjectArtifacts(paginationContext, projectId)
                .stream().map(JarArtifactDto::fromDomain).toList();
    }

    public JarArtifactDto updateJarContent(UserContext userContext, UUID projectId, UUID artifactId, FileUpload file) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        if (!projectId.equals(jarArtifactRepository.findByArtifactId(artifactId).map(JarArtifact::getProjectId).orElse(null)))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "exists, but in different project");
        Artifact artifact = artifactService.updateArtifactContent(userContext,
                storageKeyFactory.generateJarPrefix(projectId), projectId, artifactId, file);
        JarArtifact inputArtifact = jarArtifactRepository.findByArtifactId(artifactId).orElseThrow(
                () -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "probably wrong type of artifact, (or u using wrong endpoint)"));
        return new JarArtifactDto(ArtifactDto.fromDomain(artifact), inputArtifact.getAlias());
    }

    public StreamingOutput downloadJar(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_DOWNLOAD);
        return artifactService.downloadArtifact(userContext, projectId, artifactId);
    }

}
