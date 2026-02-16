package ru.vlad2509.minionflow.api.dto.response;

import ru.vlad2509.minionflow.api.dto.request.ProjectMemberRequest;

import java.util.List;

public record ProjectMemberList(
        List<ProjectMemberRequest> members
) {
}
