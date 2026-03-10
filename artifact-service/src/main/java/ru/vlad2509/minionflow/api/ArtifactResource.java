package ru.vlad2509.minionflow.api;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.api.dto.request.ArtifactRequest;
import ru.vlad2509.minionflow.api.dto.request.PaginationParams;
import ru.vlad2509.minionflow.api.dto.response.PaginatedResponse;
import ru.vlad2509.minionflow.application.JarService;
import ru.vlad2509.minionflow.application.dto.JarArtifactDto;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Path("/artifact-service/api/projects/{projectId}/artifacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtifactResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenService tokenService;

    @Inject
    JarService jarService;

    @GET
    @Path("")
    @Authenticated
    public PaginatedResponse<JarArtifactDto> getArtifacts(@Valid @BeanParam PaginationParams params, @RestPath("projectId") UUID projectId) {
        PaginationContext context = params.toContext();
        return PaginatedResponse.of(context, jarService.getJars(tokenService.parseJwt(jwt), context, projectId));
    }

    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Authenticated
    public JarArtifactDto createArtifact(@Valid ArtifactRequest request, @RestPath("projectId") UUID projectId) {
        return jarService.createJar(tokenService.parseJwt(jwt), projectId, request.alias(), request.file());
    }

    @GET
    @Path("/{artifactId}")
    @Authenticated
    public JarArtifactDto getArtifactMetadata(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID jarId) {
        return jarService.getJarMetadata(tokenService.parseJwt(jwt), projectId, jarId);
    }

    @DELETE
    @Path("/{artifactId}")
    @Authenticated
    public RestResponse deleteArtifact(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID jarId) {
        jarService.deleteJar(tokenService.parseJwt(jwt), projectId, jarId);
        return RestResponse.noContent();
    }

    @PATCH
    @Path("/{artifactId}")
    @Authenticated
    public JarArtifactDto updateArtifactMetadata(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID jarId, String newAlias) {
        return jarService.updateJarMetadata(tokenService.parseJwt(jwt), projectId, jarId, newAlias);
    }

    @GET
    @Path("/{artifactId}/content")
    @Authenticated
    public Response getArtifactContent(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID jarId) {
        UserContext context = tokenService.parseJwt(jwt);
        ArtifactDto dto = jarService.getJarMetadata(context, projectId, jarId).artifact();
        StreamingOutput stream = jarService.downloadJar(context, projectId, jarId);

        String filename = dto.originalName().isBlank() ? "undefined.jar" : dto.originalName();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        String contentType = dto.contentType().isBlank() ? "application/octet-stream" : dto.contentType();

        return Response.ok(stream)
                .header("Content-Type", contentType)
                .header("Content-Disposition", "attachment; filename*=UTF-8''" + encoded)
                .build();
    }

    @PUT
    @Path("/{artifactId}/content")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Authenticated
    public JarArtifactDto updateArtifactContent(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID jarId, @RestForm("file") FileUpload file) {
        return jarService.updateJarContent(tokenService.parseJwt(jwt), projectId, jarId, file);
    }

}
