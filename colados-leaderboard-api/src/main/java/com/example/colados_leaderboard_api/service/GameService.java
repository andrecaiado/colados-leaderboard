package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.GameDto;
import com.example.colados_leaderboard_api.dto.ImageSubmittedMsg;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import com.example.colados_leaderboard_api.producer.MessageProducer;
import com.example.colados_leaderboard_api.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class GameService {

    private final FileHandlingService fileHandlingService;
    private final ChampionshipService championshipService;
    private final GameRepository gameRepository;
    private final MessageProducer messageProducer;

    public GameService(FileHandlingService fileHandlingService, ChampionshipService championshipService, GameRepository gameRepository, MessageProducer messageProducer) {
        this.fileHandlingService = fileHandlingService;
        this.championshipService = championshipService;
        this.gameRepository = gameRepository;
        this.messageProducer = messageProducer;
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
        this.gameRepository.save(game);

        // Handle file if present
        String newFileName = null;
        if (file != null) {
            try {
                newFileName = this.fileHandlingService.handleFile(file);

                game.setScoreboardImageName(newFileName);
                this.gameRepository.save(game);

                this.messageProducer.sendMessage(new ImageSubmittedMsg(newFileName, game.getId()));
            } catch (Exception e) {
                throw new Exception("Failed to handle file: " + e.getMessage());
            }
        }

    }
}
