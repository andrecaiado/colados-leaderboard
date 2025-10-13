package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.GamePlayedDto;
import com.example.colados_leaderboard_api.service.GamePlayedService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/games-played")
public class GamePlayedController {

    private final GamePlayedService gamePlayedService;

    public GamePlayedController(GamePlayedService gamePlayedService) {
        this.gamePlayedService = gamePlayedService;
    }

    @PostMapping()
    public void registerGamePlayed(@ModelAttribute GamePlayedDto gamePlayedDto, @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        if (file != null && file.getContentType() != null && !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
        gamePlayedService.registerGamePlayed(gamePlayedDto, file);
    }
}
