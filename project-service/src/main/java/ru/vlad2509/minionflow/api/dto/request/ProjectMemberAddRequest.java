package ru.vlad2509.minionflow.api.dto.request;

import ru.vlad2509.minionflow.domain.enums.MemberRole;

public record ProjectMemberAddRequest(
        String username,
        MemberRole memberRole
) {
}
