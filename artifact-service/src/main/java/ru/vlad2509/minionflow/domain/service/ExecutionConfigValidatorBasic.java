package ru.vlad2509.minionflow.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.domain.exception.ExecutionConfigException;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;

@ApplicationScoped
public class ExecutionConfigValidatorBasic implements ExecutionConfigValidator {
    @Override
    public void validate(ExecutionConfigContent config) throws ExecutionConfigException {

    }
}
