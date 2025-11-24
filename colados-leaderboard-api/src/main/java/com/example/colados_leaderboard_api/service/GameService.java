package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.configuration.AppConstants;
import com.example.colados_leaderboard_api.dto.*;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.entity.GameResult;
import com.example.colados_leaderboard_api.entity.Player;
import com.example.colados_leaderboard_api.enums.GameResultsStatus;
import com.example.colados_leaderboard_api.enums.ImageProcessingStatus;
import com.example.colados_leaderboard_api.enums.GameResultsInputMethod;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import com.example.colados_leaderboard_api.event.GameResultsCreatedFromProcessedMsg;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.exceptions.IllegalGameStateException;
import com.example.colados_leaderboard_api.exceptions.IncompleteGameResultsException;
import com.example.colados_leaderboard_api.mapper.GameMapper;
import com.example.colados_leaderboard_api.mapper.GameResultMapper;
import com.example.colados_leaderboard_api.mapper.ImageProcessedMsgMapper;
import com.example.colados_leaderboard_api.producer.MessageProducer;
import com.example.colados_leaderboard_api.repository.GameRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
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
    private final AppConstants appConstants;

    public GameService(FileService fileService, ChampionshipService championshipService, GameRepository gameRepository, MessageProducer messageProducer, ApplicationEventPublisher publisher, ImageProcessedMsgMapper imageProcessedMsgMapper, PlayerService playerService, AppConstants appConstants) {
        this.fileService = fileService;
        this.championshipService = championshipService;
        this.gameRepository = gameRepository;
        this.messageProducer = messageProducer;
        this.publisher = publisher;
        this.imageProcessedMsgMapper = imageProcessedMsgMapper;
        this.playerService = playerService;
        this.appConstants = appConstants;
    }

    public void registerGame(RegisterGameDto registerGameDto, MultipartFile file) throws Exception {
        // Validate championship exists
        Championship championship = championshipService.getById(registerGameDto.getChampionshipId());

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
                game.setImageProcessingStatus(ImageProcessingStatus.SUBMITTED);
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
        game.setGameResultsInputMethod(GameResultsInputMethod.IMAGE_PROCESSING);
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
        game.getGameResults().forEach(gameResult -> gameResult.setPlayer(
            playerService.getByCharacter(
                    gameResult.getCharacterName(),
                    gameResult.getGame().getPlayedAt()
            )
        )
        );
        this.gameRepository.save(game);
    }

    private Game getGameById(Integer id) throws EntityNotFound {
        Optional<Game> gameOpt = this.gameRepository.findById(id);
        if (gameOpt.isEmpty()) {
            throw new EntityNotFound("Game not found with ID: " + id);
        }
        return gameOpt.get();
    }

    public GameDto getGame(Integer id) throws EntityNotFound {
        return GameMapper.toDto(this.getGameById(id));
    }

    public Iterable<GameDto> getAllGames() {
        Iterable<Game> games = this.gameRepository.findAll(Sort.by(Sort.Direction.DESC, "playedAt"));
        return GameMapper.toDtoList(games);
    }

    public Iterable<GameResultDto> getGameResults(Integer gameId) throws EntityNotFound {
        Game game = this.getGameById(gameId);
        List<GameResult> gameResults = game.getGameResults();
        gameResults.sort(Comparator.comparingInt(dto -> dto.getPosition() != null ? dto.getPosition() : 0));
        return GameResultMapper.toDtoList(gameResults);
    }

    public void updateGameResultsStatus(Integer id, PatchGameResultsStatus patchGameResultsStatus) throws EntityNotFound, IncompleteGameResultsException {
        Game game = getGameById(id);
        if (patchGameResultsStatus.getGameResultsStatus() == GameResultsStatus.ACCEPTED) {
            // Ensure all game results are complete before accepting
            validateGameResultsForAcceptance(game);
        }
        game.setGameResultsStatus(patchGameResultsStatus.getGameResultsStatus());
        this.gameRepository.save(game);
    }

    private static void validateGameResultsForAcceptance(Game game) throws IncompleteGameResultsException {
        for (GameResult result : game.getGameResults()) {
            if (result.getGame() == null || result.getPlayer() == null || result.getPosition() == null || result.getScore() == null || result.getCharacterName() == null) {
                throw new IncompleteGameResultsException("Cannot accept game results with incomplete data.");
            }
        }
    }

    public void updateGame(Integer id, UpdateGameDto updateGameDto) throws EntityNotFound, IllegalGameStateException {
        Game game = this.getGameById(id);

        // If ImageProcessingStatus is SUBMITTED, do not allow updates
        if (game.getImageProcessingStatus() == ImageProcessingStatus.SUBMITTED) {
            throw new IllegalGameStateException("Cannot update game results while image processing is not finished.");
        }

        // Update basic game info
        Championship championship = championshipService.getById(updateGameDto.getChampionshipId());
        game.setChampionship(championship);
        game.setPlayedAt(updateGameDto.getPlayedAt());

        // Clear existing results
        game.getGameResults().clear();

        // Add updated results
        for (UpdateGameResultDto resultDto : updateGameDto.getGameResults()) {
            GameResult gameResult = new GameResult();
            gameResult.setGame(game);
            Player player = null;
            if (resultDto.getPlayerId() == null && resultDto.getUserId() != null) {
                // If playerId is not provided, but userId is, try to fetch player by userId and playedAt
                player = playerService.getByUserId(
                                resultDto.getUserId(),
                                game.getPlayedAt()
                        );
            } else if (resultDto.getPlayerId() != null) {
                // If playerId is provided, fetch player by playerId
                player = playerService.getById(resultDto.getPlayerId());
            }
            gameResult.setPlayer(player);
            gameResult.setCharacterName(
                    player != null ? player.getCharacterName() : null
            );
            gameResult.setPosition(resultDto.getPosition());
            gameResult.setScore(resultDto.getScore());
            gameResult.setMaxScoreAchieved(resultDto.getScore() != null && resultDto.getScore() >= appConstants.getGameMaxScore());
            game.getGameResults().add(gameResult);
        }

        // Update game result input method
        game.setGameResultsInputMethod(GameResultsInputMethod.MANUAL);

        this.gameRepository.save(game);
    }
}
