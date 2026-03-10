package ru.vlad2509.minionflow.api.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.net.URI;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(

        int httpsStatus,
        String title,
        String code,
        String description,
        URI instance,
        String traceId,
        List<FieldError> errors

) {
    public record FieldError(String field, String message) {}

}
