package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.GamePlayedDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GamePlayedService {

    private final FileProcessingService fileProcessingService;

    public GamePlayedService(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    public void registerGamePlayed(GamePlayedDto gamePlayedDto, MultipartFile file) throws Exception {
        // Register game played

        // Process file if present
        if (file != null) {
            try {
                this.fileProcessingService.processFile(file);
            } catch (Exception e) {
                throw new Exception("Failed to process file: " + e.getMessage());
            }
        }
    }
}
