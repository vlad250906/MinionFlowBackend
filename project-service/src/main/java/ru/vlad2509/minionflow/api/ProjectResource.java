package ru.vlad2509.minionflow.api;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.dto.request.ProjectInfoRequest;
import ru.vlad2509.minionflow.application.ProjectService;
import ru.vlad2509.minionflow.application.TokenService;
import ru.vlad2509.minionflow.application.dto.ProjectInfo;
import ru.vlad2509.minionflow.api.dto.response.ProjectList;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;

import java.util.UUID;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenService tokenService;

    @Inject
    ProjectService projectService;

    @GET
    @Path("")
    @Authenticated
    public ProjectList getProjects() {
        return new ProjectList(projectService.getProjects(tokenService.parseJwt(jwt)));
    }

    @POST
    @Path("")
    @Authenticated
    public ProjectInfo createProject(ProjectInfoRequest dto) {
        return projectService.createProject(tokenService.parseJwt(jwt), dto.name(), dto.description());
    }

    @GET
    @Path("/{projectId}")
    @Authenticated
    public ProjectInfo getProject(@RestPath("projectId") UUID id) {
        return projectService.getProjectInfo(tokenService.parseJwt(jwt), id);
    }

    @DELETE
    @Path("/{projectId}")
    @Authenticated
    public RestResponse deleteProject(@RestPath("projectId") UUID id) {
        projectService.deleteProject(tokenService.parseJwt(jwt), id);
        return RestResponse.noContent();
    }

    @PATCH
    @Path("/{projectId}")
    @Authenticated
    public ProjectInfo updateProject(@RestPath("projectId") UUID id, ProjectInfoRequest dto) {
        return projectService.updateProject(tokenService.parseJwt(jwt), id, dto.name(), dto.description());
    }

}
