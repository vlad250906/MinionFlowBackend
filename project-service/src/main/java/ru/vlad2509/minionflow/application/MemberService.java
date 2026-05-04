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
import ru.vlad2509.minionflow.domain.Member;
import ru.vlad2509.minionflow.domain.Project;
import ru.vlad2509.minionflow.domain.enums.MemberRole;
import ru.vlad2509.minionflow.domain.enums.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.messaging.events.MemberChangeEventPublisher;
import ru.vlad2509.minionflow.infrastructure.persistence.model.MemberEntity;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ProjectEntity;
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

//        if (role == MemberRole.OWNER)
//            throw new ApiException(ApiError.OWNER_CONFLICT);

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND));
        Member member = new Member(project, user.userId, role, user.username);
        memberRepository.create(member);
        onMemberUpdate(new ProjectMemberChange(projectId, user.userId, role));

        return new ProjectMember(projectId, member.getUserId(), user.username,
                member.getRole().toString(), member.getMemberSince());
    }

    @Transactional
    public ProjectMember updateMember(UserContext context, UUID projectId, UUID userId, MemberRole role) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_MEMBER_UPDATE);

        if (userId.equals(context.userId()) && role != MemberRole.OWNER)
            throw new ApiException(ApiError.OWNER_LEAVE, "only another owner can remove your owner role");

        Member member = memberRepository.findByProjectUserId(projectId, userId)
                .orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND, "not a member of project"));

        member.setRole(role);
        memberRepository.updateRole(member);
        onMemberUpdate(new ProjectMemberChange(projectId, userId, role));

        return new ProjectMember(projectId, userId, member.getRemoteUsername() == null ? "@undefined" : member.getRemoteUsername(),
                role.toString(), member.getMemberSince());
    }

    @Transactional
    public void deleteMember(UserContext context, UUID projectId, UUID userId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_DELETE);

        if (userId.equals(context.userId()))
            throw new ApiException(ApiError.OWNER_LEAVE, "only another owner can kick you from project");

        if (memberRepository.deleteByProjectUser(projectId, userId) <= 0)
            throw new ApiException(ApiError.PROJECT_NOT_FOUND);

        onMemberUpdate(new ProjectMemberChange(projectId, userId, null));
    }

    public ProjectMember getMember(UserContext context, UUID projectId, UUID userId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_READ);

        Member member = memberRepository.findByProjectUserId(projectId, userId)
                .orElseThrow(() -> new ApiException(ApiError.MEMBER_NOT_FOUND));
        return new ProjectMember(projectId, userId, member.getRemoteUsername() == null ? "@undefined" : member.getRemoteUsername(),
                member.getRole().toString(), member.getMemberSince());
    }

    public List<ProjectMember> getMembers(PaginationContext paginationContext, UserContext userContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.PROJECT_READ);

        return memberRepository.findAllMembers(paginationContext, projectId).stream()
                .map(memberEntity -> new ProjectMember(projectId, memberEntity.getUserId(),
                        memberEntity.getRemoteUsername() == null ? "@undefined" : memberEntity.getRemoteUsername(),
                        memberEntity.getRole().toString(), memberEntity.getMemberSince())).toList();
    }

    private void onMemberUpdate(ProjectMemberChange change) {
        memberChangeEventPublisher.publish(change);
    }


}
