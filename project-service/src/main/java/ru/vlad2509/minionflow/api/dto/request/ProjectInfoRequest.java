package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.vlad2509.minionflow.domain.vo.ProjectNameVo;

public record ProjectInfoRequest(
        @Valid @NotNull
        ProjectNameVo name,
        String description
) {
}
