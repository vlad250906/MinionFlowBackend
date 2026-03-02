package ru.vlad2509.minionflow.application.util;

import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Path;
import java.util.UUID;

@ApplicationScoped
public class StorageKeyFactory {

    public String generateJarPrefix(UUID projectId) {
        return Path.of(projectId.toString(), "jars").toString();
    }

    public String generateInputPrefix(UUID projectId) {
        return Path.of(projectId.toString(), "inputs").toString();
    }

    public String generateExecutionConfigPrefix(UUID projectId) {
        return Path.of(projectId.toString(), "executionConfigs").toString();
    }

    public String generateOutputsPrefix(UUID projectId, UUID taskId) {
        return Path.of(projectId.toString(), "tasks", taskId.toString(), "outputs").toString();
    }

}
