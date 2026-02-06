package ru.vlad2509.minionflow.api.auth;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.auth.dto.request.RecoveryBeginRequest;
import ru.vlad2509.minionflow.api.auth.dto.request.RecoveryEndRequest;
import ru.vlad2509.minionflow.application.auth.RecoveryService;

@Path("/recovery")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RecoveryResource {

    @Inject
    RecoveryService recoveryService;

    @POST
    @Path("/begin")
    public RestResponse beginRecovery(@Valid RecoveryBeginRequest request){
        recoveryService.beginRecovery(request.email());
        return RestResponse.noContent();
    }

    @POST
    @Path("/end")
    public RestResponse endRecovery(@Valid RecoveryEndRequest request){
        recoveryService.endRecovery(request.userId(), request.verificationToken(), request.password());

        NewCookie cookie = new NewCookie.Builder("refreshJWT").maxAge(0).build();
        return RestResponse.ResponseBuilder.noContent().cookie(cookie).build();
    }

}
