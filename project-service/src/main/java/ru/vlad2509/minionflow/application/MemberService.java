package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.ProjectMember;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.messaging.ProjectMemberChange;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.domain.MemberRole;
import ru.vlad2509.minionflow.domain.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.messaging.events.MemberChangeEventPublisher;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Member;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Project;
import ru.vlad2509.minionflow.infrastructure.persistence.model.RemoteUser;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.MemberRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.ProjectRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.RemoteUserRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MemberService {

    @Inject
    MemberRepository memberRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    TokenService tokenService;

    @Inject
    MemberChangeEventPublisher memberChangeEventPublisher;

    @Inject
    RemoteUserRepository remoteUserRepository;


    @Transactional
    public ProjectMember addMember(UserContext context, UUID projectId, String username, MemberRole role) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_MEMBER_ADD_DELETE);

        RemoteUser user = remoteUserRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ApiError.USERNAME_NOT_FOUND));

        if (memberRepository.findByProjectUserId(projectId, user.userId).isPresent())
            throw new ApiException(ApiError.ALREADY_MEMBER);

        if (role == MemberRole.OWNER)
            throw new ApiException(ApiError.OWNER_CONFLICT);

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND));
        Member member = new Member(project, user.userId, role);
        memberRepository.persist(member);
        onMemberUpdate(new ProjectMemberChange(projectId, user.userId, role));

        return new ProjectMember(projectId, member.userId, user.username == null ? "@undefined" : user.username,
                member.role.toString(), member.memberSince);
    }

    @Transactional
    public ProjectMember updateMember(UserContext context, UUID projectId, UUID userId, MemberRole role) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_MEMBER_UPDATE);

        if (userId.equals(context.userId()) && role != MemberRole.OWNER)
            throw new ApiException(ApiError.OWNER_LEAVE, "project suicide denied 2.0");

        Member member = memberRepository.findByProjectUserId(projectId, userId)
                .orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND, "not a member of project"));

        member.role = role;
        onMemberUpdate(new ProjectMemberChange(projectId, userId, role));

        return new ProjectMember(projectId, userId, member.remoteUser.username == null ? "@undefined" : member.remoteUser.username,
                role.toString(), member.memberSince);
    }

    @Transactional
    public void deleteMember(UserContext context, UUID projectId, UUID userId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_DELETE);

        if (userId.equals(context.userId()))
            throw new ApiException(ApiError.OWNER_LEAVE, "project suicide denied");

        if (memberRepository.deleteByProjectUser(projectId, userId) <= 0)
            throw new ApiException(ApiError.PROJECT_NOT_FOUND);

        onMemberUpdate(new ProjectMemberChange(projectId, userId, null));
    }

    public ProjectMember getMember(UserContext context, UUID projectId, UUID userId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_READ);

        Member member = memberRepository.findByProjectUserId(projectId, userId)
                .orElseThrow(() -> new ApiException(ApiError.MEMBER_NOT_FOUND));
        return new ProjectMember(projectId, userId, member.remoteUser.username == null ? "@undefined" : member.remoteUser.username,
                member.role.toString(), member.memberSince);
    }

    public List<ProjectMember> getMembers(PaginationContext paginationContext, UserContext userContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.PROJECT_READ);

        return memberRepository.findAllMembers(paginationContext, projectId).stream()
                .map(member -> new ProjectMember(projectId, member.userId,
                        member.remoteUser.username == null ? "@undefined" : member.remoteUser.username,
                        member.role.toString(), member.memberSince)).toList();
    }

    private void onMemberUpdate(ProjectMemberChange change) {
        memberChangeEventPublisher.publish(change);
    }


}
