package ru.vlad2509.minionflow.infrastructure.engine.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.vlad2509.minionflow.domain.model.enums.InputType;

public enum EngineInputType {
    @JsonProperty("jsonl")
    JSONL;

    public static EngineInputType fromDomain(InputType inputType) {
        return switch (inputType){
            case JSONL -> JSONL;
            default -> null;
        };
    }
}