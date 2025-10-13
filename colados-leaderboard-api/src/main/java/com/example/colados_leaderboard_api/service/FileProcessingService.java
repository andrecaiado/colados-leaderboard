package com.example.colados_leaderboard_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileProcessingService {

    @Value("${minio.secure:false}")
    private String secure ;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final StorageService storageService;

    public FileProcessingService(StorageService storageService) {
        this.storageService = storageService;
    }

    public void processFile(MultipartFile file) throws IOException {
        // Logic to process the file can be added here
        String fileName = this.buildFileName(Objects.requireNonNull(file.getOriginalFilename()));

        // After processing, upload the file to the storage service
        try {
            this.storageService.uploadFile(bucketName, fileName, file.getInputStream(), file.getContentType());
        } catch (IOException e) {
            throw new IOException("Failed to upload file: " + e.getMessage());
        }
        // Additional processing logic
        // Add record to database


    }

    private String buildFileName(String originalFilename) {
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }
        return UUID.randomUUID().toString().concat(fileExtension);
    }
}
