package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.ProjectInfo;
import ru.vlad2509.minionflow.application.dto.ProjectInfoShort;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.messaging.ProjectMemberChange;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.domain.Member;
import ru.vlad2509.minionflow.domain.Project;
import ru.vlad2509.minionflow.domain.enums.MemberRole;
import ru.vlad2509.minionflow.domain.vo.ProjectNameVo;
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
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    TokenService tokenService;

    @Inject
    MemberRepository memberRepository;

    @Inject
    MemberChangeEventPublisher memberChangeEventPublisher;

    @Transactional
    public ProjectInfo createProject(UserContext context, ProjectNameVo projectName, String projectDescription) {
//        if (projectRepository.findByName(projectName).isPresent())
//            throw new ApiException(ApiError.PROJECT_ALREADY_EXISTS);

        Project project = new Project(projectName, projectDescription);
        Member member = new Member(project, context.userId(), MemberRole.OWNER, null);

        projectRepository.create(project);
        memberRepository.create(member);
        memberChangeEventPublisher.publish(new ProjectMemberChange(project.getId(), member.getUserId(), member.getRole()));

        return new ProjectInfo(project.getId(), projectName, project.getProjectDescription());
    }

    @Transactional
    public ProjectInfo updateProject(UserContext context, UUID projectId, ProjectNameVo projectName, String projectDescription) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_UPDATE_GENERAL);

//        if (projectRepository.findByName(projectName).isPresent())
//            throw new ApiException(ApiError.PROJECT_ALREADY_EXISTS);

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND));
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        projectRepository.update(project);

        return new ProjectInfo(projectId, project.getProjectNameVo(), project.getProjectDescription());
    }

    @Transactional
    public void deleteProject(UserContext context, UUID projectId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_DELETE);
        if (projectRepository.deleteById(projectId) <= 0)
            throw new ApiException(ApiError.PROJECT_NOT_FOUND);
    }

    public ProjectInfo getProjectInfo(UserContext context, UUID projectId) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_READ);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND));

        return new ProjectInfo(projectId, project.getProjectNameVo(), project.getProjectDescription());
    }

    public List<ProjectInfoShort> getProjects(PaginationContext paginationContext, UserContext userContext) {
        return memberRepository.findAllProjects(paginationContext, userContext.userId()).stream()
                .map(project -> new ProjectInfoShort(project.getId(), project.getProjectNameVo())).toList();
    }

}
