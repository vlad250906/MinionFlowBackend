package ru.vlad2509.minionflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(implementation = String.class)
public record EmailVo(
        @Size(max = 100)
        @NotBlank
        @Email
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email contain illegal symbols"
        )
        String value
) {

    public EmailVo(String value) {
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
    public static EmailVo create(String raw) {
        return new EmailVo(raw);
    }

}
