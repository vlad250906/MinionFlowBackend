package ru.vlad2509.minionflow.api;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestPath;
import ru.vlad2509.minionflow.application.dto.engine.MicrotaskLogsBatch;
import ru.vlad2509.minionflow.application.TaskService;
import ru.vlad2509.minionflow.application.util.TokenService;

import java.util.UUID;

@Path("/artifact-service/api/projects/{projectId}/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LogResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    TaskService taskService;

    @Inject
    TokenService tokenService;

    @GET
    @Path("/{microtaskId}")
    @Authenticated
    public MicrotaskLogsBatch getLogs(@RestPath("projectId") UUID projectId, @RestPath("microtaskId") UUID microtaskId,
                                      @DefaultValue("-1") @QueryParam("afterSeq") int afterSeq,
                                      @DefaultValue("0") @QueryParam("limit") int limit) {
        return taskService.getLogs(tokenService.parseJwt(jwt), projectId, microtaskId, afterSeq, limit);
    }

}
