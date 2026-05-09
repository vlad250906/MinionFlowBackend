package ru.vlad2509.minionflow.infrastructure.engine.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EngineDestinationType {
    @JsonProperty("s3")
    S3
}