package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.service.ProcessedFileService;
import com.example.colados_leaderboard_api.service.StorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/processed-files")
public class ProcessedFileController {

    private final ProcessedFileService processedFileService;

    public ProcessedFileController(ProcessedFileService processedFileService) {
        this.processedFileService = processedFileService;
    }

    @PostMapping()
    public void handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        processedFileService.processFile(file);
    }
}
