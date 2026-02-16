package ru.vlad2509.minionflow.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(implementation = String.class)
public record ProjectNameVo(
        @Size(min = 3, max = 200)
        @NotBlank
        String value
) {

    public ProjectNameVo(String value) {
        this.value = value;
    }

    public boolean isNullOrEmpty(){
        return value == null || value.isEmpty();
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ProjectNameVo create(String raw) {
        return new ProjectNameVo(raw);
    }

}
