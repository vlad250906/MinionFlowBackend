package ru.vlad2509.minionflow.api.dto.response;

import ru.vlad2509.minionflow.application.dto.ProjectMember;

import java.util.List;

public record ProjectMemberList(
        List<ProjectMember> members
) {
}
