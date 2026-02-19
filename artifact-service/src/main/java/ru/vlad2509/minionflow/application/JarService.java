package ru.vlad2509.minionflow.application;

import io.vertx.codegen.doc.Token;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.domain.ArtifactType;
import ru.vlad2509.minionflow.domain.ProjectPermission;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class JarService {

    @Inject
    ArtifactService artifactService;

    @Inject
    TokenService tokenService;

    public ArtifactDto createJar(UserContext userContext, UUID projectId, String alias, FileUpload file) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        return artifactService.createArtifact(userContext, projectId, alias, file, ArtifactType.JAR);
    }

    public ArtifactDto updateJarMetadata(UserContext userContext, UUID projectId, UUID artifactId, String alias) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        return artifactService.updateArtifactMetadata(userContext, artifactId, alias);
    }

    public void deleteJar(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        artifactService.deleteArtifact(userContext, artifactId);
    }

    public ArtifactDto getJarMetadata(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_READ);
        return artifactService.getArtifactMetadata(userContext, artifactId);
    }

    public List<ArtifactDto> getJars(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_READ);
        return artifactService.getArtifacts(userContext, paginationContext, projectId);
    }

    public ArtifactDto updateJarContent(UserContext userContext, UUID projectId, UUID artifactId, FileUpload file) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_WRITE);
        return artifactService.updateArtifactContent(userContext, projectId, artifactId, file);
    }

    public StreamingOutput downloadJar(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.JAR_READ);
        return artifactService.downloadArtifact(userContext, artifactId);
    }

}
