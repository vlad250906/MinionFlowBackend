package ru.vlad2509.minionflow.api.dto.request;

import ru.vlad2509.minionflow.domain.MemberRole;

public record ProjectMemberUpdateRequest (
        MemberRole memberRole
) {
}
