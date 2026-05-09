package ru.vlad2509.minionflow.infrastructure.engine.dto;

import ru.vlad2509.minionflow.domain.model.execution.ExecutionConfigContent;
import ru.vlad2509.minionflow.infrastructure.engine.dto.input.EngineInputSpec;
import ru.vlad2509.minionflow.infrastructure.engine.dto.output.EngineOutputSpec;

public record EngineTaskConfiguration (
        EngineExecutionSpec execution,
        EngineInputSpec input,
        EngineOutputSpec output,
        EngineSecuritySpec security
) {
}
