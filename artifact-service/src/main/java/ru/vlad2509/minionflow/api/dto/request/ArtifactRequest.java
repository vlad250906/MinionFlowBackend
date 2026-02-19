package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public record ArtifactRequest(
        @RestForm("alias")
        @NotBlank
        @Size(min = 1, max = 100)
        String alias,

        @RestForm("file")
        FileUpload file

) {
}
