package ru.vlad2509.minionflow.infrastructure.s3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.application.ports.out.S3Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class S3ServiceImplementation implements S3Service {

    @ConfigProperty(name = "artifact-service.bucket_name", defaultValue = "minionflow")
    String bucketName;

    @Inject
    S3Client s3;

    @Override
    public boolean upload(String key, FileUpload file) {
        if (key == null || key.isBlank() || file == null)
            return false;
        Path tmpPath = file.uploadedFile();

        try {
            PutObjectRequest.Builder req = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key);

            String contentType = file.contentType();
            if (contentType != null && !contentType.isBlank()) {
                req = req.contentType(contentType);
            }
            try {
                req = req.contentLength(Files.size(tmpPath));
            } catch (IOException ignored) {
            }

            s3.putObject(req.build(), RequestBody.fromFile(tmpPath));
            return true;
        } catch (SdkException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public StreamingOutput download(String key) {
        if (key == null || key.isBlank())
            throw new ApiException(ApiError.UNEXPECTED_ERROR);

        return output -> {
            try (var in = s3.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build())) {
                in.transferTo(output);
            } catch (S3Exception e) {
                e.printStackTrace();
                // TODO: нормально обработать
                if (e.statusCode() == 404)
                    throw new ApiException(ApiError.UNEXPECTED_ERROR);
                throw new ApiException(ApiError.S3_UNAVAILABLE);
            } catch (SdkException e) {
                throw new ApiException(ApiError.S3_UNAVAILABLE);
            }
        };
    }

    @Override
    public boolean delete(String key) {
        if (key == null || key.isBlank())
            return false;

        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (SdkException e) {
            e.printStackTrace();
            return false;
        }
    }
}
