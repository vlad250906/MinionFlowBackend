package ru.vlad2509.minionflow.application.dto.messaging;

import ru.vlad2509.minionflow.domain.model.enums.MemberRole;
import java.util.UUID;

public record ProjectMemberChange(
        UUID projectId,
        UUID userId,
        MemberRole newMemberRole
) {
}
