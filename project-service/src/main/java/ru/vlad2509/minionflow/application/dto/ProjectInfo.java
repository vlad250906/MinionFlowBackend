package ru.vlad2509.minionflow.application.dto;

import ru.vlad2509.minionflow.domain.ProjectNameVo;

import java.util.UUID;

public record ProjectInfo(
        UUID projectId,
        ProjectNameVo projectName,
        String projectDescription
) {
}
