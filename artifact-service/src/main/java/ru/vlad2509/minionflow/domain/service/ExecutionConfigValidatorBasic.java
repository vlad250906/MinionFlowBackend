package ru.vlad2509.minionflow.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.domain.exception.ExecutionConfigException;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;
import ru.vlad2509.minionflow.domain.model.execution.ExecutionType;

@ApplicationScoped
public class ExecutionConfigValidatorBasic implements ExecutionConfigValidator {
    @Override
    public void validate(ExecutionConfigContent config) throws ExecutionConfigException {
        if(config.type() == ExecutionType.SWARM_SYNC && config.swarm() == null)
            throw new ExecutionConfigException("SWARM could not be null in swarm-sync execution config!");
    }
}
