package com.example.colados_leaderboard_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ProcessedFileService {

    @Value("${minio.secure:false}")
    private String secure ;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final StorageService storageService;

    public ProcessedFileService(StorageService storageService) {
        this.storageService = storageService;
    }

    public void processFile(MultipartFile file) throws IOException {
        // Logic to process the file can be added here
        String fileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        InputStream fileData = file.getInputStream();
        String contentType = file.getContentType();

        // After processing, upload the file to the storage service
        this.storageService.uploadFile(bucketName, fileName, fileData, contentType);
    }
}
