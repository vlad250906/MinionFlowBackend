package ru.vlad2509.minionflow.domain.service;

import ru.vlad2509.minionflow.domain.exception.ExecutionConfigException;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;

public interface ExecutionConfigValidator {

    void validate(ExecutionConfigContent config) throws ExecutionConfigException;

}
