package ru.vlad2509.minionflow.api.rest;


import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.dto.request.PaginationParams;
import ru.vlad2509.minionflow.api.dto.request.TaskCreateRequest;
import ru.vlad2509.minionflow.api.dto.response.OutputList;
import ru.vlad2509.minionflow.api.dto.response.PaginatedResponse;
import ru.vlad2509.minionflow.application.TaskService;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.TaskRunDto;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmAgent;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.dto.light.TaskRunLight;
import ru.vlad2509.minionflow.application.util.TokenService;

import java.util.UUID;

@Path("/api/projects/{projectId}/tasks")
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
    @Path("/{taskId}/outputs")
    @Authenticated
    public OutputList getOutputs(@RestPath("projectId") UUID projectId,
                                 @RestPath("taskId") UUID taskId) {
        return new OutputList(taskService.getOutputs(tokenService.parseJwt(jwt), projectId, taskId));
    }

    @GET
    @Path("/{taskId}/stats/stateless")
    @Authenticated
    public StatelessTaskState getStatelessState(@RestPath("projectId") UUID projectId,
                                                @RestPath("taskId") UUID taskId) {
        return taskService.getStatelessState(tokenService.parseJwt(jwt), projectId, taskId);
    }

    @GET
    @Path("/{taskId}/microtasks/stateless/{microtaskId}")
    @Authenticated
    public StatelessMicrotaskRun getStatelessMicrotask(@RestPath("projectId") UUID projectId,
                                                       @RestPath("taskId") UUID taskId,
                                                       @RestPath("microtaskId") UUID microtaskId) {
        return taskService.getStatelessMicrotask(tokenService.parseJwt(jwt), projectId, taskId, microtaskId);
    }

    @GET
    @Path("/{taskId}/stats/swarm")
    @Authenticated
    public SwarmTaskState getSwarmState(@RestPath("projectId") UUID projectId,
                                        @RestPath("taskId") UUID taskId) {
        return taskService.getSwarmState(tokenService.parseJwt(jwt), projectId, taskId);
    }

    @GET
    @Path("/{taskId}/microtasks/swarm/{microtaskId}")
    @Authenticated
    public SwarmMicrotaskRun getSwarmMicrotask(@RestPath("projectId") UUID projectId,
                                               @RestPath("taskId") UUID taskId,
                                               @RestPath("microtaskId") UUID microtaskId) {
        return taskService.getSwarmMicrotask(tokenService.parseJwt(jwt), projectId, taskId, microtaskId);
    }

    @GET
    @Path("/{taskId}/agents/{agentId}")
    @Authenticated
    public SwarmAgent getSwarmAgent(@RestPath("projectId") UUID projectId,
                                    @RestPath("taskId") UUID taskId,
                                    @RestPath("agentId") UUID agentId) {
        return taskService.getSwarmAgent(tokenService.parseJwt(jwt), projectId, taskId, agentId);
    }


}
