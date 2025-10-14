package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.GameDto;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import com.example.colados_leaderboard_api.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class GameService {

    private final FileHandlingService fileHandlingService;
    private final ChampionshipService championshipService;
    private final GameRepository gameRepository;

    public GameService(FileHandlingService fileHandlingService, ChampionshipService championshipService, GameRepository gameRepository) {
        this.fileHandlingService = fileHandlingService;
        this.championshipService = championshipService;
        this.gameRepository = gameRepository;
    }

    public void registerGame(GameDto gameDto, MultipartFile file) throws Exception {
        // Validate championship exists
        Optional<Championship> championship = championshipService.findById(gameDto.getChampionshipId());
        if (championship.isEmpty()) {
            throw new Exception("Championship not found with ID: " + gameDto.getChampionshipId());
        }

        // Register game played
        Game game = new Game();
        game.setChampionship(championship.get());
        game.setStatusForEdition(StatusForEdition.OPEN);

        // Handle file if present
        if (file != null) {
            try {
                String fileName = this.fileHandlingService.handleFile(file);
                game.setScoreboardImageName(fileName);
            } catch (Exception e) {
                throw new Exception("Failed to handle file: " + e.getMessage());
            }
        }

        this.gameRepository.save(game);
    }
}
