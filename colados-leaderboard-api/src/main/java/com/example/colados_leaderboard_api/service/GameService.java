package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.GameDto;
import com.example.colados_leaderboard_api.dto.ImageSubmittedMsg;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import com.example.colados_leaderboard_api.producer.MessageProducer;
import com.example.colados_leaderboard_api.repository.GameRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class GameService {

    private final FileService fileService;
    private final ChampionshipService championshipService;
    private final GameRepository gameRepository;
    private final MessageProducer messageProducer;

    public GameService(FileService fileService, ChampionshipService championshipService, GameRepository gameRepository, MessageProducer messageProducer) {
        this.fileService = fileService;
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
        if (file != null) {
            try {
                String newFileName = this.fileService.uploadFileToStorage(file);

                game.setScoreboardImageName(newFileName);
                this.gameRepository.save(game);

                this.messageProducer.sendMessage(new ImageSubmittedMsg(newFileName, game.getId()));
            } catch (IOException e) {
                throw new Exception("Failed to handle file: " + e.getMessage());
            } catch (AmqpException e) {
                throw new Exception("Failed to send message to queue: " + e.getMessage());
            }
        }
    }
}
