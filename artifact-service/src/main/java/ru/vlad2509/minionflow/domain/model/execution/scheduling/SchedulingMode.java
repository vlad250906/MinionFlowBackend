package ru.vlad2509.minionflow.domain.model.execution.scheduling;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SchedulingMode {

    @JsonProperty("fixed")
    FIXED,
    @JsonProperty("asap")
    ASAP

}
