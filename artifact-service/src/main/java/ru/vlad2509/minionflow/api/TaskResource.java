package ru.vlad2509.minionflow.api;


import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.dto.request.ArtifactRequest;
import ru.vlad2509.minionflow.api.dto.request.PaginationParams;
import ru.vlad2509.minionflow.api.dto.request.TaskCreateRequest;
import ru.vlad2509.minionflow.api.dto.response.PaginatedResponse;
import ru.vlad2509.minionflow.application.JarService;
import ru.vlad2509.minionflow.application.TaskService;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ArtifactDto;
import ru.vlad2509.minionflow.application.dto.InputArtifactDto;
import ru.vlad2509.minionflow.application.dto.TaskRunDto;
import ru.vlad2509.minionflow.application.dto.light.TaskRunLight;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.TokenService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Path("/artifact-service/api/projects/{projectId}/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenService tokenService;

    @Inject
    TaskService taskService;

    @GET
    @Path("")
    @Authenticated
    public PaginatedResponse<TaskRunLight> getTasks(@Valid @BeanParam PaginationParams params, @RestPath("projectId") UUID projectId) {
        PaginationContext context = params.toContext();
        return PaginatedResponse.of(context, taskService.getTaskRuns(tokenService.parseJwt(jwt), context, projectId));
    }

    @POST
    @Path("")
    @Authenticated
    public TaskRunDto createTask(@Valid TaskCreateRequest request, @RestPath("projectId") UUID projectId) {
        return taskService.createTaskRun(tokenService.parseJwt(jwt), projectId, request.jarId(), request.inputId(), request.configId());
    }

    @GET
    @Path("/{taskId}")
    @Authenticated
    public TaskRunDto getTask(@RestPath("projectId") UUID projectId, @RestPath("taskId") UUID taskId) {
        return taskService.getTaskRun(tokenService.parseJwt(jwt), projectId, taskId);
    }

    @PATCH
    @Path("/{taskId}")
    @Authenticated
    public RestResponse cancelTask(@RestPath("projectId") UUID projectId, @RestPath("taskId") UUID taskId) {
        taskService.cancelTaskRun(tokenService.parseJwt(jwt), projectId, taskId);
        return RestResponse.noContent();
    }

    @GET
    @Path("/{taskId}/output")
    @Authenticated
    public ArtifactDto getTaskOutputMeta(@RestPath("projectId") UUID projectId, @RestPath("taskId") UUID taskId) {
        return taskService.getOutputMetadata(tokenService.parseJwt(jwt), projectId, taskId);
    }

    @GET
    @Path("/{taskId}/output/content")
    @Authenticated
    public Response getTaskOutputContent(@RestPath("projectId") UUID projectId, @RestPath("taskId") UUID taskId) {
        UserContext context = tokenService.parseJwt(jwt);
        ArtifactDto dto = taskService.getOutputMetadata(context, projectId, taskId);
        StreamingOutput stream = taskService.getOutputContent(context, projectId, taskId);

        String filename = dto.originalName().isBlank() ? "undefined.jsonl" : dto.originalName();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        String contentType = dto.contentType().isBlank() ? "application/octet-stream" : dto.contentType();

        return Response.ok(stream)
                .header("Content-Type", contentType)
                .header("Content-Disposition", "attachment; filename*=UTF-8''" + encoded)
                .build();
    }

}
