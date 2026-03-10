package ru.vlad2509.minionflow.application.ports.out;

import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface S3Service {

    boolean upload(String key, FileUpload file);

    boolean upload(String key, Path filePath, String contentType);

    List<S3Object> enumerateFiles(String prefix);

    StreamingOutput download(String key);

    boolean delete(String key);


    record S3Object(String key, long size){
    }

}
