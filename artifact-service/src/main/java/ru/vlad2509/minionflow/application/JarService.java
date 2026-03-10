package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.dto.InputArtifactDto;
import ru.vlad2509.minionflow.application.dto.JarArtifactDto;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.ArtifactService;
import ru.vlad2509.minionflow.application.util.StorageKeyFactory;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.domain.model.ArtifactType;
import ru.vlad2509.minionflow.domain.model.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifact;
import ru.vlad2509.minionflow.infrastructure.persistence.model.JarArtifact;
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
        ArtifactDto artifactDto = artifactService.createArtifact(userContext, storageKeyFactory.generateJarPrefix(projectId),
                projectId, file, ArtifactType.JAR);
        if (jarArtifactRepository.createJarArtifact(artifactDto.artifactId(), alias) == null)
            throw new ApiException(ApiError.UNEXPECTED_ERROR, "artifact should've been created, but it wasn't");
        return JarArtifactDto.fromDto(artifactDto, alias);
    }

    public JarArtifactDto updateJarMetadata(UserContext userContext, UUID projectId, UUID artifactId, String alias) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        JarArtifact jarArtifact = jarArtifactRepository.update(artifactId, alias)
                .orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "it should've been found, possible desync or bug??"));
        return JarArtifactDto.fromDto(ArtifactDto.fromJpa(jarArtifact.artifact), jarArtifact.alias);
    }

    public void deleteJar(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        artifactService.deleteArtifact(userContext, artifactId);
    }

    public JarArtifactDto getJarMetadata(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_READ);
        ArtifactDto dto = artifactService.getArtifactMetadata(userContext, artifactId);
        JarArtifact jarArtifact = jarArtifactRepository.findByArtifactId(artifactId).orElseThrow(
                () -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "it should've been found, possible desync or bug??"));

        return JarArtifactDto.fromDto(dto, jarArtifact.alias);
    }

    public List<JarArtifactDto> getJars(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_READ);
        return jarArtifactRepository.findAllProjectArtifacts(paginationContext, projectId)
                .stream().map(ja -> JarArtifactDto.fromDto(ArtifactDto.fromJpa(ja.artifact), ja.alias)).toList();
    }

    public JarArtifactDto updateJarContent(UserContext userContext, UUID projectId, UUID artifactId, FileUpload file) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        ArtifactDto artifactDto = artifactService.updateArtifactContent(userContext, storageKeyFactory.generateJarPrefix(projectId),
                projectId, artifactId, file);
        JarArtifact inputArtifact = jarArtifactRepository.findByArtifactId(artifactId).orElseThrow(
                () -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "it should've been found, possible desync or bug??"));
        return JarArtifactDto.fromDto(artifactDto, inputArtifact.alias);
    }

    public StreamingOutput downloadJar(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_READ);
        return artifactService.downloadArtifact(userContext, artifactId);
    }

}
