package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.domain.model.InputType;

public record InputArtifactRequest(

        @RestForm("alias")
        @NotBlank
        @Size(min = 1, max = 100)
        String alias,

        @RestForm("inputType")
        @NotNull
        InputType inputType,

        @RestForm("file")
        FileUpload file
) {
}
