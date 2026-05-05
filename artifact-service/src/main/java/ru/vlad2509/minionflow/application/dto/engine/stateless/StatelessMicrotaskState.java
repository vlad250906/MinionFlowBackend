package ru.vlad2509.minionflow.application.dto.engine.stateless;

import ru.vlad2509.minionflow.application.dto.engine.MicrotaskRunStatus;

import java.util.UUID;

public record StatelessMicrotaskState(

        UUID microtaskId,
        int displayIndex,
        MicrotaskRunStatus status

) {
}
