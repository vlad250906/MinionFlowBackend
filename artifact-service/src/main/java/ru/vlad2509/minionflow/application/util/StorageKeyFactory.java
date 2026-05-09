package ru.vlad2509.minionflow.application.util;

import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Path;
import java.util.UUID;

@ApplicationScoped
public class StorageKeyFactory {

    public String generateJarPrefix(UUID projectId) {
        return projectId.toString() + "/jars";
    }

    public String generateInputPrefix(UUID projectId) {
        return projectId.toString() + "/inputs";
    }

    public String generateExecutionConfigPrefix(UUID projectId) {
        return projectId.toString() + "/executionConfigs";
    }

    public String generateOutputsPrefix(UUID projectId, UUID taskId) {
        return projectId.toString() + "/tasks/" + taskId.toString() + "/outputs";
    }

    public String extractLastFromPath(String prefix){
        String[] chunks = prefix.split("/");
        return chunks[chunks.length - 1];
    }

}
