package ru.vlad2509.minionflow.api;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.application.dto.ProjectMember;
import ru.vlad2509.minionflow.api.dto.response.ProjectMemberList;
import ru.vlad2509.minionflow.api.dto.request.ProjectMemberRequest;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;

import java.util.UUID;

@Path("/projects/{projectId}/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MemberResource {

    @GET
    @Path("")
    @Authenticated
    public ProjectMemberList getMembers(@RestPath("projectId") UUID projectId) {
        // TODO
        throw new ApiException(ApiError.I_AM_A_TEAPOT, projectId.toString());
    }

    // TODO: Система инвайтов с оповещением по почте??
    @POST
    @Path("")
    @Authenticated
    public ProjectMember addMember(@RestPath("projectId") UUID projectId, ProjectMemberRequest dto) {
        // TODO
        throw new ApiException(ApiError.I_AM_A_TEAPOT);
    }

    @GET
    @Path("/{userId}")
    @Authenticated
    public ProjectMember getMember(@RestPath("projectId") UUID projectId, @PathParam("userId") UUID userId) {
        // TODO
        throw new ApiException(ApiError.I_AM_A_TEAPOT);
    }

    @DELETE
    @Path("/{userId}")
    @Authenticated
    public RestResponse removeMember(@RestPath("projectId") UUID projectId, @PathParam("userId") UUID userId) {
        // TODO
        throw new ApiException(ApiError.I_AM_A_TEAPOT);
    }

    @PATCH
    @Path("/{userId}")
    @Authenticated
    public ProjectMember changeMemberRole(@RestPath("projectId") UUID projectId, @PathParam("userId") UUID userId, ProjectMemberRequest dto) {
        // TODO
        throw new ApiException(ApiError.I_AM_A_TEAPOT);
    }

}
