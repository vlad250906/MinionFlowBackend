package ru.vlad2509.minionflow.application.ports.out;

import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.InputStream;
import java.nio.file.Path;

public interface S3Service {

    boolean upload(String key, FileUpload file);

    boolean upload(String key, Path filePath, String contentType);

    long getFileSize(String key);

    StreamingOutput download(String key);

    boolean delete(String key);

}
