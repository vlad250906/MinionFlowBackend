package ru.vlad2509.minionflow.api.dto.response;

import ru.vlad2509.minionflow.application.dto.ProjectInfoShort;

import java.util.List;

public record ProjectList(
        List<ProjectInfoShort> projects
) {
}
