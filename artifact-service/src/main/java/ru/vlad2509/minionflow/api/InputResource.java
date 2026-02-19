package ru.vlad2509.minionflow.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;

@Path("/artifact-service/api/projects/{projectId}/inputs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InputResource {

    @GET
    @Path("/test")
    public void test(){
        // TODO
        throw new ApiException(ApiError.I_AM_A_TEAPOT);
    }

}
