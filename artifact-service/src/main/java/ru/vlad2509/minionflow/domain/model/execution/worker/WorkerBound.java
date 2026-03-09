package ru.vlad2509.minionflow.domain.model.execution.worker;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WorkerBound {
    @JsonProperty("io")
    IO,
    @JsonProperty("cpu")
    CPU
}