package ru.vlad2509.minionflow.api;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import ru.vlad2509.minionflow.api.dto.request.ProjectMemberAddRequest;
import ru.vlad2509.minionflow.api.dto.request.ProjectMemberUpdateRequest;
import ru.vlad2509.minionflow.application.MemberService;
import ru.vlad2509.minionflow.application.TokenService;
import ru.vlad2509.minionflow.application.dto.ProjectMember;
import ru.vlad2509.minionflow.api.dto.response.ProjectMemberList;

import java.util.List;
import java.util.UUID;

@Path("/projects/{projectId}/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MemberResource {

    @Inject
    MemberService memberService;

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenService tokenService;

    @GET
    @Path("")
    @Authenticated
    public ProjectMemberList getMembers(@RestPath("projectId") UUID projectId) {
        List<ProjectMember> members = memberService.getMembers(tokenService.parseJwt(jwt), projectId);
        return new ProjectMemberList(members);
    }

    // TODO: Система инвайтов с оповещением по почте??
    @POST
    @Path("")
    @Authenticated
    public ProjectMember addMember(@RestPath("projectId") UUID projectId, ProjectMemberAddRequest dto) {
        // FIXME: преобразование username в userId и проверка!!
        return memberService.addMember(tokenService.parseJwt(jwt), projectId, UUID.fromString(dto.username()), dto.memberRole());
    }

    @GET
    @Path("/{userId}")
    @Authenticated
    public ProjectMember getMember(@RestPath("projectId") UUID projectId, @PathParam("userId") UUID userId) {
        return memberService.getMember(tokenService.parseJwt(jwt), projectId, userId);
    }

    @DELETE
    @Path("/{userId}")
    @Authenticated
    public RestResponse removeMember(@RestPath("projectId") UUID projectId, @PathParam("userId") UUID userId) {
        memberService.deleteMember(tokenService.parseJwt(jwt), projectId, userId);
        return RestResponse.noContent();
    }

    @PATCH
    @Path("/{userId}")
    @Authenticated
    public ProjectMember changeMemberRole(@RestPath("projectId") UUID projectId, @PathParam("userId") UUID userId, ProjectMemberUpdateRequest dto) {
        return memberService.updateMember(tokenService.parseJwt(jwt), projectId, userId, dto.memberRole());
    }

}
