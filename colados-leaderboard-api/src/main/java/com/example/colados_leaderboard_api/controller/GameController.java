package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.GameDto;
import com.example.colados_leaderboard_api.service.GameService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void registerGame(@RequestPart("game") GameDto gameDto, @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        if (file != null && file.getContentType() != null && !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
        gameService.registerGame(gameDto, file);
    }
}
