package ru.vlad2509.minionflow.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import org.jboss.resteasy.reactive.RestCookie;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.dto.response.JwtPairResponse;
import ru.vlad2509.minionflow.api.dto.request.LoginRequest;
import ru.vlad2509.minionflow.api.dto.request.RefreshRequest;
import ru.vlad2509.minionflow.api.dto.response.LoginResponse;
import ru.vlad2509.minionflow.application.AuthService;
import ru.vlad2509.minionflow.application.dto.TokenPair;
import ru.vlad2509.minionflow.application.util.TokenService;

@Path("/identity-service/api/sessions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    @Inject
    AuthService authService;

    @Inject
    TokenService tokenService;

    @POST
    @Path("")
    public RestResponse<LoginResponse> login(@Valid LoginRequest request) {
        TokenPair pair = authService.login(request.email(), request.username(), request.password());

        NewCookie cookie = new NewCookie.Builder("refreshJWT")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(tokenService.getRefreshTokenTtl())
                .value(pair.refreshJWT())
                .build();

        return RestResponse.ResponseBuilder
                .ok(new LoginResponse(pair.userId(), pair.accessJWT(), pair.issuedAt().plusSeconds(tokenService.getAccessTokenTtl())))
                .cookie(cookie)
                .build();
    }

    @POST
    @Path("/refresh")
    public RestResponse<JwtPairResponse> refresh(@Valid RefreshRequest request,
                                                 @RestCookie("refreshJWT") String refreshJWT) {
        TokenPair pair = authService.refreshToken(refreshJWT);

        NewCookie cookie = new NewCookie.Builder("refreshJWT")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(tokenService.getRefreshTokenTtl())
                .value(pair.refreshJWT())
                .build();

        return RestResponse.ResponseBuilder
                .ok(new JwtPairResponse(pair.accessJWT(), pair.issuedAt().plusSeconds(tokenService.getAccessTokenTtl())))
                .cookie(cookie)
                .build();
    }

    @DELETE
    @Path("/me")
    public RestResponse logout(@Valid RefreshRequest request,
                               @RestCookie("refreshJWT") String refreshJWT) {
        authService.logout(refreshJWT);
        NewCookie cookie = new NewCookie.Builder("refreshJWT").maxAge(0).build();
        return RestResponse.ResponseBuilder.noContent().cookie(cookie).build();
    }

    @DELETE
    @Path("")
    public RestResponse logoutAll(@Valid RefreshRequest request,
                                  @RestCookie("refreshJWT") String refreshJWT) {
        authService.logoutAll(refreshJWT);
        NewCookie cookie = new NewCookie.Builder("refreshJWT").maxAge(0).build();
        return RestResponse.ResponseBuilder.noContent().cookie(cookie).build();
    }


//    @Inject
//    PasswordService passwordService;
//
//    @GET
//    @Path("/test")
//    public String hash(@QueryParam("password") String password) {
//        if (password == null)
//            return "";
//        return passwordService.hashNew(password);
//    }

}
