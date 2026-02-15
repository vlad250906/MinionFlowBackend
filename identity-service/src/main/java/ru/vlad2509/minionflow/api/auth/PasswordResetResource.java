package ru.vlad2509.minionflow.api.auth;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.auth.dto.request.RecoveryBeginRequest;
import ru.vlad2509.minionflow.api.auth.dto.request.RecoveryEndRequest;
import ru.vlad2509.minionflow.application.auth.RecoveryService;

@Path("/password-resets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PasswordResetResource {

    @Inject
    RecoveryService recoveryService;

    @POST
    @Path("")
    public RestResponse beginRecovery(@Valid RecoveryBeginRequest request){
        recoveryService.beginRecovery(request.email());
        return RestResponse.noContent();
    }

    @PUT
    @Path("")
    public RestResponse endRecovery(@Valid RecoveryEndRequest request){
        recoveryService.endRecovery(request.userId(), request.verificationToken(), request.password());

        NewCookie cookie = new NewCookie.Builder("refreshJWT").maxAge(0).build();
        return RestResponse.ResponseBuilder.noContent().cookie(cookie).build();
    }

}
