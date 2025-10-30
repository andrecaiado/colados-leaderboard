package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.GameDto;
import com.example.colados_leaderboard_api.dto.ImageProcessedMsg;
import com.example.colados_leaderboard_api.dto.ImageSubmittedMsg;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.enums.ImageProcessingStatus;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import com.example.colados_leaderboard_api.event.GameResultsCreatedFromProcessedMsg;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.mapper.ImageProcessedMsgMapper;
import com.example.colados_leaderboard_api.producer.MessageProducer;
import com.example.colados_leaderboard_api.repository.GameRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher publisher;
    private final ImageProcessedMsgMapper imageProcessedMsgMapper;
    private final PlayerService playerService;

    public GameService(FileService fileService, ChampionshipService championshipService, GameRepository gameRepository, MessageProducer messageProducer, ApplicationEventPublisher publisher, ImageProcessedMsgMapper imageProcessedMsgMapper, PlayerService playerService) {
        this.fileService = fileService;
        this.championshipService = championshipService;
        this.gameRepository = gameRepository;
        this.messageProducer = messageProducer;
        this.publisher = publisher;
        this.imageProcessedMsgMapper = imageProcessedMsgMapper;
        this.playerService = playerService;
    }

    public void registerGame(GameDto gameDto, MultipartFile file) throws Exception {
        // Validate championship exists
        Championship championship = championshipService.getById(gameDto.getChampionshipId());

        // Register game played
        Game game = new Game();
        game.setChampionship(championship);
        game.setStatusForEdition(StatusForEdition.OPEN);
        this.gameRepository.save(game);

        // Handle file if present
        if (file != null) {
            try {
                String newFileName = this.fileService.uploadFileToStorage(file);

                game.setScoreboardImageName(newFileName);
                this.gameRepository.save(game);

                this.messageProducer.sendMessage(new ImageSubmittedMsg(newFileName));
            } catch (IOException e) {
                throw new Exception("Failed to handle file: " + e.getMessage());
            } catch (AmqpException e) {
                throw new Exception("Failed to send message to queue: " + e.getMessage());
            }
        }
    }

    public void updateGameFromProcessedMsg(ImageProcessedMsg imageProcessedMsg) throws Exception {
        Optional<Game> gameOpt = this.getGameByScoreboardImageName(imageProcessedMsg.getFile_name());
        if (gameOpt.isEmpty()) {
            throw new EntityNotFound("Game not found with image name: " + imageProcessedMsg.getFile_name());
        }

        Game game = gameOpt.get();
        try {
            game.setImageProcessingStatus(ImageProcessingStatus.valueOf(imageProcessedMsg.getStatus().toUpperCase()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid image processing status: " + imageProcessedMsg.getStatus());
        }
        if (game.getImageProcessingStatus() == ImageProcessingStatus.PROCESSED) {
            game.setGameResults(imageProcessedMsgMapper.mapToGameResults(imageProcessedMsg.getResults(), game));
        }
        this.gameRepository.save(game);

        // If the image was processed successfully, publish an event
        if (game.getImageProcessingStatus() == ImageProcessingStatus.PROCESSED) {
            this.publisher.publishEvent(new GameResultsCreatedFromProcessedMsg(this, game.getId()));
        }
    }

    private Optional<Game> getGameByScoreboardImageName(String imageName) {
        return this.gameRepository.findByScoreboardImageName(imageName);
    }

    public void updateGameResultsPlayers(Integer gameId) throws EntityNotFound {
        Optional<Game> gameOpt = this.gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            throw new EntityNotFound("Game not found with ID: " + gameId);
        }

        Game game = gameOpt.get();
        game.getGameResults().forEach(gameResult -> {
            gameResult.setPlayer(
                playerService.getPlayerByCharacter(
                        gameResult.getCharacterName(),
                        gameResult.getGame().getPlayedAt()
                )
            );
        }
        );
        this.gameRepository.save(game);
    }
}
