package ru.vlad2509.minionflow.domain.service;

import ru.vlad2509.minionflow.domain.exception.ExecutionConfigException;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfig;

public interface ExecutionConfigValidator {

    void validate(ExecutionConfig config) throws ExecutionConfigException;

}
