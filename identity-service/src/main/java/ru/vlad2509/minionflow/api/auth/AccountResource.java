package ru.vlad2509.minionflow.api.auth;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestCookie;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.auth.dto.request.PasswordChangeRequest;
import ru.vlad2509.minionflow.api.auth.dto.request.RegisterRequest;
import ru.vlad2509.minionflow.api.auth.dto.request.UserInfoChangeRequest;
import ru.vlad2509.minionflow.api.auth.dto.response.RegisterResponse;
import ru.vlad2509.minionflow.application.auth.AccountService;
import ru.vlad2509.minionflow.application.dto.UserInfo;

import java.util.UUID;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    AccountService accountService;

    @Inject
    JsonWebToken accessJWT;

    @POST
    @Path("/accounts")
    public RegisterResponse register(@Valid RegisterRequest request) {
        UUID userId = accountService.register(request.email(), request.username(), request.password());
        boolean verificationRequired = accountService.isVerificationRequired(userId);
        return new RegisterResponse(userId, verificationRequired);
    }

    @POST
    @Path("/account-activations")
    public void activate(@RestQuery("accountId") UUID accountId, @RestQuery("activationToken") UUID activationToken) {
        accountService.verifyRegistration(accountId, activationToken);
    }

    @PATCH
    @Path("/accounts/me/passwords")
    public RestResponse passwordChange(@Valid PasswordChangeRequest request,
                                       @RestCookie("refreshJWT") String refreshJWT) {
        accountService.changePassword(refreshJWT, request.oldPassword(), request.newPassword());

        NewCookie cookie = new NewCookie.Builder("refreshJWT").maxAge(0).build();
        return RestResponse.ResponseBuilder.noContent().cookie(cookie).build();
    }

    @PATCH
    @Path("/accounts/me")
    public void userInfoChange(@Valid UserInfoChangeRequest request,
                               @RestCookie("refreshJWT") String refreshJWT) {
        accountService.changeUsername(refreshJWT, request.newUsername());
    }

    @GET
    @Path("/accounts/me")
    @Authenticated
    public UserInfo userInfo() {
        return accountService.getUserInfo(UUID.fromString(accessJWT.getSubject()));
    }

}
