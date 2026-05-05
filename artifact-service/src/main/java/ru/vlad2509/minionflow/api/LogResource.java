package ru.vlad2509.minionflow.api;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestPath;
import ru.vlad2509.minionflow.api.dto.response.MicrotaskLogsResponse;
import java.util.UUID;

@Path("/artifact-service/api/projects/{projectId}/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LogResource {

    @GET
    @Path("/{microtaskId}")
    @Authenticated
    public MicrotaskLogsResponse getLogs(@RestPath("projectId") UUID projectId, @RestPath("microtaskId") UUID microtaskId,
                                        @DefaultValue("-1") @QueryParam("afterSeq") int afterSeq,
                                        @DefaultValue("0") @QueryParam("limit") int limit) {
        // TODO
        return null;
    }

}
