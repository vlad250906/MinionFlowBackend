package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.dto.InputArtifactDto;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.ArtifactService;
import ru.vlad2509.minionflow.application.util.StorageKeyFactory;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.domain.ArtifactType;
import ru.vlad2509.minionflow.domain.InputType;
import ru.vlad2509.minionflow.domain.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifact;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.InputArtifactRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class InputService {

    @Inject
    ArtifactService artifactService;

    @Inject
    TokenService tokenService;

    @Inject
    StorageKeyFactory storageKeyFactory;

    @Inject
    InputArtifactRepository inputArtifactRepository;

    public InputArtifactDto createInput(UserContext userContext, UUID projectId, String alias, InputType type, FileUpload file) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_WRITE);
        ArtifactDto artifact = artifactService.createArtifact(userContext, storageKeyFactory.generateJarPrefix(projectId),
                projectId, alias, file, ArtifactType.INPUT);
        if (inputArtifactRepository.createInputArtifact(artifact.artifactId(), type) == null)
            throw new ApiException(ApiError.UNEXPECTED_ERROR, "artifact should've been created, but it wasn't");
        return InputArtifactDto.fromDto(artifact, type);
    }

    public InputArtifactDto updateInputMetadata(UserContext userContext, UUID projectId, UUID artifactId,
                                                String alias, InputType type) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_WRITE);
        ArtifactDto artifact = artifactService.updateArtifactMetadata(userContext, artifactId, alias);
        if (!inputArtifactRepository.updateType(artifactId, type))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "it should've been found, possible desync or bug??");
        return InputArtifactDto.fromDto(artifact, type);
    }

    public void deleteInput(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_WRITE);
        artifactService.deleteArtifact(userContext, artifactId);
    }

    public InputArtifactDto getInputMetadata(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_READ);
        ArtifactDto dto = artifactService.getArtifactMetadata(userContext, artifactId);
        InputArtifact inputArtifact = inputArtifactRepository.findByArtifactId(artifactId).orElseThrow(
                () -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "it should've been found, possible desync or bug??"));

        return InputArtifactDto.fromDto(dto, inputArtifact.type);
    }

    public List<InputArtifactDto> getInputs(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_READ);
        return inputArtifactRepository.findAllProjectArtifacts(paginationContext, projectId).stream()
                .map(ia -> InputArtifactDto.fromDto(ArtifactDto.fromJpa(ia.artifact), ia.type)).toList();
    }

    public InputArtifactDto updateInputContent(UserContext userContext, UUID projectId, UUID artifactId, FileUpload file) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_WRITE);
        ArtifactDto dto = artifactService.updateArtifactContent(userContext, storageKeyFactory.generateInputPrefix(projectId),
                projectId, artifactId, file);
        InputArtifact inputArtifact = inputArtifactRepository.findByArtifactId(artifactId).orElseThrow(
                () -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "it should've been found, possible desync or bug??"));
        return InputArtifactDto.fromDto(dto, inputArtifact.type);
    }

    public StreamingOutput downloadInput(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_READ);
        return artifactService.downloadArtifact(userContext, artifactId);
    }

}
