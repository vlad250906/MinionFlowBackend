package ru.vlad2509.minionflow.application.dto.engine;

import java.time.Instant;

public record MicrotaskLog(String loglevel, long seq, Instant timestamp, String message) {
}
