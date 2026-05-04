package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.vo.ProjectNameVo;

import java.util.UUID;

public record ProjectInfoShort(
        UUID projectId,
        ProjectNameVo projectName
) {
}
