package ru.vlad2509.minionflow.api;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.dto.request.ExecutionConfigRequest;
import ru.vlad2509.minionflow.api.dto.request.PaginationParams;
import ru.vlad2509.minionflow.api.dto.response.PaginatedResponse;
import ru.vlad2509.minionflow.application.ExecutionConfigService;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.ExecutionConfigDto;
import ru.vlad2509.minionflow.application.dto.ExecutionConfigShort;
import ru.vlad2509.minionflow.application.util.TokenService;
import java.util.UUID;

@Path("/artifact-service/api/projects/{projectId}/executionConfigs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExecutionConfigResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenService tokenService;

    @Inject
    ExecutionConfigService executionConfigService;

    @GET
    @Path("")
    @Authenticated
    public PaginatedResponse<ExecutionConfigShort> getConfigs(@Valid @BeanParam PaginationParams params, @RestPath("projectId") UUID projectId) {
        PaginationContext context = params.toContext();
        return PaginatedResponse.of(context, executionConfigService.getExecutionConfigs(tokenService.parseJwt(jwt), context, projectId));
    }

    @POST
    @Path("")
    @Authenticated
    public ExecutionConfigDto createConfig(@Valid ExecutionConfigRequest request, @RestPath("projectId") UUID projectId) {
        return executionConfigService.createExecutionConfig(tokenService.parseJwt(jwt), projectId, request.alias(), request.config());
    }

    @GET
    @Path("/{configId}")
    @Authenticated
    public ExecutionConfigDto getConfig(@RestPath("projectId") UUID projectId, @RestPath("configId") UUID configId) {
        return executionConfigService.getExecutionConfig(tokenService.parseJwt(jwt), projectId, configId);
    }

    @DELETE
    @Path("/{configId}")
    @Authenticated
    public RestResponse deleteConfig(@RestPath("projectId") UUID projectId, @RestPath("configId") UUID configId) {
        executionConfigService.deleteExecutionConfig(tokenService.parseJwt(jwt), projectId, configId);
        return RestResponse.noContent();
    }

    @PATCH
    @Path("/{configId}")
    @Authenticated
    public ExecutionConfigDto updateConfig(@RestPath("projectId") UUID projectId, @RestPath("configId") UUID configId,
                                           @Valid ExecutionConfigRequest body) {
        return executionConfigService.updateExecutionConfig(tokenService.parseJwt(jwt), projectId, configId, body.alias(), body.config());
    }

}
