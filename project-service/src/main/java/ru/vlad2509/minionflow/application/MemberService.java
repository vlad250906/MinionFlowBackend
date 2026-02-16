package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.dto.ProjectMember;
import ru.vlad2509.minionflow.application.dto.UserContext;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.domain.MemberRole;
import ru.vlad2509.minionflow.domain.ProjectNameVo;
import ru.vlad2509.minionflow.domain.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Member;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Project;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.MemberRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.ProjectRepository;

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


    @Transactional
    public void addMember(UserContext context, UUID projectId, UUID userId, MemberRole role) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_MEMBER_ADD_DELETE);

        if (memberRepository.findByProjectUserId(projectId, userId).isPresent())
            throw new ApiException(ApiError.ALREADY_MEMBER);

        if (role == MemberRole.OWNER)
            throw new ApiException(ApiError.OWNER_CONFLICT);

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND));
        Member member = new Member(project, userId, role);
        memberRepository.persist(member);
    }

    @Transactional
    public void updateMember(UserContext context, UUID projectId, UUID userId, MemberRole role) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_MEMBER_UPDATE);

        Member member = memberRepository.findByProjectUserId(projectId, context.userId())
                .orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND, "not a member of project"));

        member.role = role;
    }

    @Transactional
    public void deleteMember(UserContext context, UUID projectId, UUID userId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_DELETE);

        if (userId.equals(context.userId()))
            throw new ApiException(ApiError.OWNER_LEAVE, "project suicide denied");

        if (memberRepository.deleteByProjectUser(projectId, userId) <= 0)
            throw new ApiException(ApiError.PROJECT_NOT_FOUND);
    }

    public ProjectMember getMember(UserContext context, UUID projectId, UUID userId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_READ);

        Member member = memberRepository.findByProjectUserId(projectId, context.userId())
                .orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND, "wow, race condition... idc"));
        return new ProjectMember(projectId, userId, member.role.toString(), member.memberSince);
    }

    public List<ProjectMember> getMembers(UserContext context, UUID projectId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_READ);

        return memberRepository.findAllMembers(projectId).stream()
                .map(member -> new ProjectMember(projectId, member.userId, member.role.toString(), member.memberSince)).toList();
    }


}
