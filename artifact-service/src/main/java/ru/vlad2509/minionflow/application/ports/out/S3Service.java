package ru.vlad2509.minionflow.application.ports.out;

import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.InputStream;

public interface S3Service {

    boolean upload(String key, FileUpload file);

    StreamingOutput download(String key);

    boolean delete(String key);

}
