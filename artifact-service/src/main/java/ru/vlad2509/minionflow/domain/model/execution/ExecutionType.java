package ru.vlad2509.minionflow.domain.model.execution;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ExecutionType {

    @JsonProperty("stateless")
    STATELESS,
    @JsonProperty("stateful")
    STATEFUL

}
