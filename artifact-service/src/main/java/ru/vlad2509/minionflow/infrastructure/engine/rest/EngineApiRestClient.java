package ru.vlad2509.minionflow.infrastructure.engine.rest;


import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import jakarta.ws.rs.core.MediaType;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmAgent;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.infrastructure.engine.dto.EngineCreateTaskRunRequest;

import java.util.UUID;

@RegisterRestClient(configKey = "engine-api")
@Consumes(MediaType.APPLICATION_JSON)
public interface EngineApiRestClient {

    @POST
    @Path("/api/tasks/execute")
    @Produces(MediaType.TEXT_PLAIN)
    String runTask(EngineCreateTaskRunRequest createTaskRunRequest);

    @GET
    @Path("/api/tasks/swarm/{taskId}/state")
    @Produces(MediaType.APPLICATION_JSON)
    SwarmTaskState getSwarmState(@PathParam("taskId") UUID taskId);

    @GET
    @Path("/api/tasks/swarm/{taskId}/microtasks/{microtaskId}")
    @Produces(MediaType.APPLICATION_JSON)
    SwarmMicrotaskRun getSwarmMicrotask(@PathParam("taskId") UUID taskId, @PathParam("microtaskId") UUID microtaskId);

    @GET
    @Path("/api/tasks/swarm/{taskId}/agents/{agentId}")
    @Produces(MediaType.APPLICATION_JSON)
    SwarmAgent getSwarmAgent(@PathParam("taskId") UUID taskId, @PathParam("agentId") UUID agentId);

    @GET
    @Path("/api/tasks/stateless/{taskId}/state")
    @Produces(MediaType.APPLICATION_JSON)
    StatelessTaskState getStatelessState(@PathParam("taskId") UUID taskId);

    @GET
    @Path("/api/tasks/stateless/{taskId}/microtasks/{microtaskId}")
    @Produces(MediaType.APPLICATION_JSON)
    StatelessMicrotaskRun getStatelessMicrotask(@PathParam("taskId") UUID taskId, @PathParam("microtaskId") UUID microtaskId);

    @ClientExceptionMapper
    @Blocking
    static ApiException mapException(Response response) {
        int status = response.getStatus();
        String body = "";
        try {
            body = response.readEntity(String.class);
        } catch (Exception ignored) {
        }

        return new ApiException(status, "nope", status >= 500 ? "" : body);
    }

}