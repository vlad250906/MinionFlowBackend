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
import ru.vlad2509.minionflow.domain.model.Artifact;
import ru.vlad2509.minionflow.domain.model.InputArtifact;
import ru.vlad2509.minionflow.domain.model.enums.ArtifactType;
import ru.vlad2509.minionflow.domain.model.enums.InputType;
import ru.vlad2509.minionflow.domain.model.enums.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.persistence.model.InputArtifactEntity;
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
        Artifact artifact = artifactService.createArtifact(userContext, storageKeyFactory.generateInputPrefix(projectId),
                projectId, file, ArtifactType.INPUT);
        InputArtifact inputArtifact = new InputArtifact(artifact, alias, type);
        inputArtifactRepository.createInputArtifact(inputArtifact);

        return InputArtifactDto.fromDomain(inputArtifact);
    }

    public InputArtifactDto updateInputMetadata(UserContext userContext, UUID projectId, UUID artifactId,
                                                String alias, InputType type) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_WRITE);
        InputArtifact inputArtifact = inputArtifactRepository.findByArtifactId(artifactId)
                .orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "input artifact not found"));
        if (!projectId.equals(inputArtifact.getProjectId()))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "could exists, but in different project");
        inputArtifact.setAlias(alias);
        inputArtifact.setInputType(type);
        inputArtifactRepository.update(inputArtifact);
        return InputArtifactDto.fromDomain(inputArtifact);
    }

    public void deleteInput(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_WRITE);
        if (!projectId.equals(inputArtifactRepository.findByArtifactId(artifactId).map(InputArtifact::getProjectId).orElse(null)))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "could exists, but in different project");
        artifactService.deleteArtifact(userContext, artifactId);
    }

    public InputArtifactDto getInputMetadata(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_READ);
        InputArtifact inputArtifact = inputArtifactRepository.findByArtifactId(artifactId)
                .orElseThrow(() -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "input artifact not found"));
        if (!projectId.equals(inputArtifact.getProjectId()))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "could exists, but in different project");
        return InputArtifactDto.fromDomain(inputArtifact);
    }

    public List<InputArtifactDto> getInputs(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_READ);
        return inputArtifactRepository.findAllProjectArtifacts(paginationContext, projectId).stream()
                .map(InputArtifactDto::fromDomain).toList();
    }

    public InputArtifactDto updateInputContent(UserContext userContext, UUID projectId, UUID artifactId, FileUpload file) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_WRITE);
        if (!projectId.equals(inputArtifactRepository.findByArtifactId(artifactId).map(InputArtifact::getProjectId).orElse(null)))
            throw new ApiException(ApiError.ARTIFACT_NOT_FOUND, "could exists, but in different project");
        Artifact artifact = artifactService.updateArtifactContent(userContext,
                storageKeyFactory.generateInputPrefix(projectId), projectId, artifactId, file);
        InputArtifact inputArtifact = inputArtifactRepository.findByArtifactId(artifact.getId()).orElseThrow(
                () -> new ApiException(ApiError.ARTIFACT_NOT_FOUND, "probably wrong type of artifact, (or u using wrong endpoint)"));
        return new InputArtifactDto(ArtifactDto.fromDomain(artifact), inputArtifact.getAlias(), inputArtifact.getInputType());
    }

    public StreamingOutput downloadInput(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.INPUT_READ);
        return artifactService.downloadArtifact(userContext, projectId, artifactId);
    }

}
