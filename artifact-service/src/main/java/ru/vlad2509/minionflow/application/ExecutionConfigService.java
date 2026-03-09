package ru.vlad2509.minionflow.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.vlad2509.minionflow.application.context.PaginationContext;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.dto.ExecutionConfigDto;
import ru.vlad2509.minionflow.application.dto.light.ExecutionConfigLight;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.util.TokenService;
import ru.vlad2509.minionflow.domain.exception.ExecutionConfigException;
import ru.vlad2509.minionflow.domain.model.ProjectPermission;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfig;
import ru.vlad2509.minionflow.domain.service.ExecutionConfigValidator;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ExecutionConfigJpa;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.ExecutionConfigRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ExecutionConfigService {

    @Inject
    ExecutionConfigRepository executionConfigRepository;

    @Inject
    TokenService tokenService;

    @Inject
    ExecutionConfigValidator executionConfigValidator;

    public ExecutionConfigDto createExecutionConfig(UserContext userContext, UUID projectId, String alias, ExecutionConfig executionConfig) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_WRITE);
        try {
            executionConfigValidator.validate(executionConfig);
        } catch (ExecutionConfigException ex) {
            throw new ApiException(ApiError.INVALID_EXECUTION_CONFIG.getHttpStatusCode(), ApiError.INVALID_EXECUTION_CONFIG.getErrorCode(), ex.getMessage());
        }
        ExecutionConfigJpa executionConfigJpa = new ExecutionConfigJpa(alias, projectId, userContext.userId(), executionConfig);
        executionConfigRepository.createExecutionConfig(executionConfigJpa);
        return ExecutionConfigDto.fromJpa(executionConfigJpa);
    }

    public ExecutionConfigDto updateExecutionConfig(UserContext userContext, UUID projectId, UUID configId, String alias, ExecutionConfig executionConfig) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_WRITE);
        try {
            executionConfigValidator.validate(executionConfig);
        } catch (ExecutionConfigException ex) {
            throw new ApiException(ApiError.INVALID_EXECUTION_CONFIG.getHttpStatusCode(), ApiError.INVALID_EXECUTION_CONFIG.getErrorCode(), ex.getMessage());
        }
        return transactionalUpdate(configId, alias, executionConfig);
    }

    public void deleteExecutionConfig(UserContext userContext, UUID projectId, UUID configId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_WRITE);
        if (executionConfigRepository.deleteById(configId) <= 0)
            throw new ApiException(ApiError.EXECUTION_CONFIG_NOT_FOUND);
    }

    public ExecutionConfigDto getExecutionConfig(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_READ);
        ExecutionConfigJpa executionConfigJpa = executionConfigRepository.findById(artifactId)
                .orElseThrow(() -> new ApiException(ApiError.EXECUTION_CONFIG_NOT_FOUND));
        return ExecutionConfigDto.fromJpa(executionConfigJpa);
    }

    public List<ExecutionConfigLight> getExecutionConfigs(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_READ);
        return executionConfigRepository.findAllProjectConfigs(paginationContext, projectId).stream()
                .map(ExecutionConfigLight::fromJpa).toList();
    }

    @Transactional
    ExecutionConfigDto transactionalUpdate(UUID configId, String alias, ExecutionConfig executionConfig) {
        ExecutionConfigJpa configJpa = executionConfigRepository.findById(configId).orElseThrow(() -> new ApiException(ApiError.EXECUTION_CONFIG_NOT_FOUND));
        configJpa.alias = alias;
        configJpa.content = executionConfig;
        return ExecutionConfigDto.fromJpa(configJpa);
    }
}
