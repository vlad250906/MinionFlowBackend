package ru.vlad2509.minionflow.api.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ru.vlad2509.minionflow.api.auth.dto.JwtPairResponse;
import ru.vlad2509.minionflow.api.auth.dto.LoginRequest;
import ru.vlad2509.minionflow.api.auth.dto.RefreshRequest;
import ru.vlad2509.minionflow.application.auth.AuthService;
import ru.vlad2509.minionflow.application.auth.PasswordService;
import ru.vlad2509.minionflow.application.dto.TokenPair;

@Path("/auth")
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public JwtPairResponse login(LoginRequest request) {
        System.out.println("login");
        System.out.println(request);
        TokenPair pair = authService.login(request.email(), request.username(), request.password());
        return new JwtPairResponse(pair.accessJWT(), pair.refreshJWT());
    }

    @POST
    @Path("/refresh")
    public JwtPairResponse refresh(RefreshRequest request) {
        TokenPair pair = authService.refreshToken(request.refreshJWT());
        return new JwtPairResponse(pair.accessJWT(), pair.refreshJWT());
    }

    @POST
    @Path("/logout")
    public void logout(RefreshRequest request) {
        authService.logout(request.refreshJWT());
    }

    @POST
    @Path("/logout-all")
    public void logoutAll(RefreshRequest request) {
        authService.logoutAll(request.refreshJWT());
    }


    @Inject
    PasswordService passwordService;

    @GET
    @Path("/test")
    public String hash(@QueryParam("password") String password) {
        if(password == null)
            return "";
        return passwordService.hashNew(password);
    }

}
