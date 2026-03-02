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
import ru.vlad2509.minionflow.api.dto.request.InputArtifactRequest;
import ru.vlad2509.minionflow.api.dto.request.InputMetaUpdateRequest;
import ru.vlad2509.minionflow.api.dto.request.PaginationParams;
import ru.vlad2509.minionflow.api.dto.response.PaginatedResponse;
import ru.vlad2509.minionflow.application.InputService;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.InputArtifactDto;
import ru.vlad2509.minionflow.application.util.TokenService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Path("/artifact-service/api/projects/{projectId}/inputs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InputResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenService tokenService;

    @Inject
    InputService inputService;

    @GET
    @Path("")
    @Authenticated
    public PaginatedResponse<InputArtifactDto> getArtifacts(@Valid @BeanParam PaginationParams params, @RestPath("projectId") UUID projectId) {
        PaginationContext context = params.toContext();
        return PaginatedResponse.of(context, inputService.getInputs(tokenService.parseJwt(jwt), context, projectId));
    }

    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Authenticated
    public InputArtifactDto createArtifact(@Valid InputArtifactRequest request, @RestPath("projectId") UUID projectId) {
        return inputService.createInput(tokenService.parseJwt(jwt), projectId, request.alias(), request.inputType(), request.file());
    }

    @GET
    @Path("/{artifactId}")
    @Authenticated
    public InputArtifactDto getArtifactMetadata(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID inputId) {
        return inputService.getInputMetadata(tokenService.parseJwt(jwt), projectId, inputId);
    }

    @DELETE
    @Path("/{artifactId}")
    @Authenticated
    public RestResponse deleteArtifact(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID inputId) {
        inputService.deleteInput(tokenService.parseJwt(jwt), projectId, inputId);
        return RestResponse.noContent();
    }

    @PATCH
    @Path("/{artifactId}")
    @Authenticated
    public InputArtifactDto updateArtifactMetadata(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID inputId,
                                                   @Valid InputMetaUpdateRequest body) {
        return inputService.updateInputMetadata(tokenService.parseJwt(jwt), projectId, inputId, body.alias(), body.inputType());
    }

    @GET
    @Path("/{artifactId}/content")
    @Authenticated
    public Response getArtifactContent(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID inputId) {
        UserContext context = tokenService.parseJwt(jwt);
        InputArtifactDto dto = inputService.getInputMetadata(context, projectId, inputId);
        StreamingOutput stream = inputService.downloadInput(context, projectId, inputId);

        String filename = dto.artifact().originalName().isBlank() ? "undefined.jar" : dto.artifact().originalName();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        String contentType = dto.artifact().contentType().isBlank() ? "application/octet-stream" : dto.artifact().contentType();

        return Response.ok(stream)
                .header("Content-Type", contentType)
                .header("Content-Disposition", "attachment; filename*=UTF-8''" + encoded)
                .build();
    }

    @PUT
    @Path("/{artifactId}/content")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Authenticated
    public InputArtifactDto updateArtifactContent(@RestPath("projectId") UUID projectId, @RestPath("artifactId") UUID inputId, @RestForm("file") FileUpload file) {
        return inputService.updateInputContent(tokenService.parseJwt(jwt), projectId, inputId, file);
    }

}
