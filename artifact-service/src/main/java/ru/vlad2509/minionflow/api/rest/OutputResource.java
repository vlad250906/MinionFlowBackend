package ru.vlad2509.minionflow.api.rest;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestPath;
import ru.vlad2509.minionflow.application.TaskService;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.util.TokenService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Path("/artifact-service/api/projects/{projectId}/outputs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OutputResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenService tokenService;

    @Inject
    TaskService taskService;



    @GET
    @Path("/{outputId}")
    @Authenticated
    public ArtifactDto getTaskOutputMeta(@RestPath("projectId") UUID projectId, @RestPath("taskId") UUID taskId,
                                         @RestPath("outputId") UUID outputId) {
        return taskService.getOutputMetadata(tokenService.parseJwt(jwt), projectId, taskId, outputId);
    }

    @GET
    @Path("/{outputId}/content")
    @Authenticated
    public Response getTaskOutputContent(@RestPath("projectId") UUID projectId, @RestPath("taskId") UUID taskId,
                                         @RestPath("outputId") UUID outputId) {
        UserContext context = tokenService.parseJwt(jwt);
        ArtifactDto dto = taskService.getOutputMetadata(context, projectId, taskId, outputId);
        StreamingOutput stream = taskService.getOutputContent(context, projectId, taskId, outputId);

        String filename = dto.originalName().isBlank() ? "undefined.jsonl" : dto.originalName();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        String contentType = dto.contentType().isBlank() ? "application/octet-stream" : dto.contentType();

        return Response.ok(stream)
                .header("Content-Type", contentType)
                .header("Content-Disposition", "attachment; filename*=UTF-8''" + encoded)
                .build();
    }

}
