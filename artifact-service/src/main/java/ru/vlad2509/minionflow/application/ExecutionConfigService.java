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
import ru.vlad2509.minionflow.domain.model.ExecutionConfig;
import ru.vlad2509.minionflow.domain.model.enums.ProjectPermission;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;
import ru.vlad2509.minionflow.domain.service.ExecutionConfigValidator;
import ru.vlad2509.minionflow.infrastructure.persistence.model.ExecutionConfigEntity;
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

    public ExecutionConfigDto createExecutionConfig(UserContext userContext, UUID projectId, String alias, ExecutionConfigContent executionConfigContent) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_WRITE);
        try {
            executionConfigValidator.validate(executionConfigContent);
        } catch (ExecutionConfigException ex) {
            throw new ApiException(ApiError.INVALID_EXECUTION_CONFIG.getHttpStatusCode(), ApiError.INVALID_EXECUTION_CONFIG.getErrorCode(), ex.getMessage());
        }
        ExecutionConfig executionConfig = new ExecutionConfig(alias, projectId, userContext.userId(), executionConfigContent);
        executionConfigRepository.create(executionConfig);
        return ExecutionConfigDto.fromDomain(executionConfig);
    }

    public ExecutionConfigDto updateExecutionConfig(UserContext userContext, UUID projectId, UUID configId, String alias, ExecutionConfigContent executionConfigContent) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_WRITE);
        try {
            executionConfigValidator.validate(executionConfigContent);
        } catch (ExecutionConfigException ex) {
            throw new ApiException(ApiError.INVALID_EXECUTION_CONFIG.getHttpStatusCode(), ApiError.INVALID_EXECUTION_CONFIG.getErrorCode(), ex.getMessage());
        }
        return transactionalUpdate(configId, alias, executionConfigContent);
    }

    public void deleteExecutionConfig(UserContext userContext, UUID projectId, UUID configId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_WRITE);
        if (executionConfigRepository.deleteById(configId) <= 0)
            throw new ApiException(ApiError.EXECUTION_CONFIG_NOT_FOUND);
    }

    public ExecutionConfigDto getExecutionConfig(UserContext userContext, UUID projectId, UUID artifactId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_READ);
        ExecutionConfig executionConfig = executionConfigRepository.findById(artifactId)
                .orElseThrow(() -> new ApiException(ApiError.EXECUTION_CONFIG_NOT_FOUND));
        return ExecutionConfigDto.fromDomain(executionConfig);
    }

    public List<ExecutionConfigLight> getExecutionConfigs(UserContext userContext, PaginationContext paginationContext, UUID projectId) {
        tokenService.authorize(userContext, projectId, ProjectPermission.CONFIG_READ);
        return executionConfigRepository.findAllProjectConfigs(paginationContext, projectId);
    }

    @Transactional
    ExecutionConfigDto transactionalUpdate(UUID configId, String alias, ExecutionConfigContent executionConfigContent) {
        ExecutionConfig config = executionConfigRepository.findById(configId).orElseThrow(() -> new ApiException(ApiError.EXECUTION_CONFIG_NOT_FOUND));
        config.setAlias(alias);
        config.setContent(executionConfigContent);
        executionConfigRepository.update(config);
        return ExecutionConfigDto.fromDomain(config);
    }
}
