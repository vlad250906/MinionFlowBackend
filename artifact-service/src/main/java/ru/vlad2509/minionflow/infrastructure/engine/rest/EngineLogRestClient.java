package ru.vlad2509.minionflow.infrastructure.engine.rest;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.stateless.StatelessTaskState;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmAgent;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmMicrotaskRun;
import ru.vlad2509.minionflow.application.dto.engine.swarm.SwarmTaskState;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.infrastructure.engine.dto.EngineCreateTaskRunRequest;

import java.util.UUID;

@RegisterRestClient(configKey = "engine-log")
@Consumes(MediaType.APPLICATION_JSON)
public interface EngineLogRestClient {

    @GET
    @Path("/api/microtasks/{microtaskId}/logs")
    @Produces(MediaType.APPLICATION_JSON)
    MicrotaskLogsBatch getLogs(@PathParam("microtaskId") UUID microtaskId,
                               @QueryParam("afterSeq") int afterSeq,
                               @QueryParam("limit") int limit);

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