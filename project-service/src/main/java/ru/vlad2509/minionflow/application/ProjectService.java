package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.dto.ProjectInfo;
import ru.vlad2509.minionflow.application.dto.ProjectInfoShort;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ProjectMemberChange;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.domain.MemberRole;
import ru.vlad2509.minionflow.domain.ProjectNameVo;
import ru.vlad2509.minionflow.domain.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.messaging.events.MemberChangeEventPublisher;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Member;
import ru.vlad2509.minionflow.infrastructure.persistence.model.Project;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.MemberRepository;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.ProjectRepository;

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
        if (projectRepository.findByName(projectName).isPresent())
            throw new ApiException(ApiError.PROJECT_ALREADY_EXISTS);

        Project project = new Project(projectName, projectDescription);
        Member member = new Member(project, context.userId(), MemberRole.OWNER);

        projectRepository.persist(project);
        memberRepository.persist(member);
        memberChangeEventPublisher.publish(new ProjectMemberChange(project.id, member.userId, member.role));

        return new ProjectInfo(project.id, project.getProjectName(), project.projectDescription);
    }

    @Transactional
    public ProjectInfo updateProject(UserContext context, UUID projectId, ProjectNameVo projectName, String projectDescription) {
        tokenService.authorize(context, projectId, ProjectPermission.PROJECT_UPDATE_GENERAL);

        if (projectRepository.findByName(projectName).isPresent())
            throw new ApiException(ApiError.PROJECT_ALREADY_EXISTS);

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND));
        project.projectName = projectName.value();
        project.projectDescription = projectDescription;

        return new ProjectInfo(projectId, project.getProjectName(), project.projectDescription);
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

        return new ProjectInfo(projectId, project.getProjectName(), project.projectDescription);
    }

    public List<ProjectInfoShort> getProjects(PaginationContext paginationContext, UserContext userContext) {
        return memberRepository.findAllProjects(paginationContext, userContext.userId()).stream()
                .map(project -> new ProjectInfoShort(project.id, project.getProjectName())).toList();
    }

}
