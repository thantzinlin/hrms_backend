package com.hrms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration for file storage (claim attachments).
 * Uses configurable base path; falls back to system temp if not set.
 */
@Configuration
public class FileStorageConfig {

    @Value("${hrms.claim.uploads.dir:${java.io.tmpdir}/hrms-claim-uploads}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create upload directory: " + uploadPath, e);
        }
    }

    public Path getUploadPath() {
        return uploadPath;
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
