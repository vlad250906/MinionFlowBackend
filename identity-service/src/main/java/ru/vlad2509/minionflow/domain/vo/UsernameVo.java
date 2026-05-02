package ru.vlad2509.minionflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(implementation = String.class)
public record UsernameVo(

        @Size(min = 3, max = 52)
        @NotBlank
        @Pattern(
                regexp = "^[\\p{L}0-9_-]+$",
                message = "Username can only contain letters, digits and _ -"
        )
        String value
) {

    public UsernameVo(String value) {
        this.value = value;
    }

    public boolean isNullOrEmpty() {
        return value == null || value.isEmpty();
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UsernameVo create(String raw) {
        return new UsernameVo(raw);
    }

}
