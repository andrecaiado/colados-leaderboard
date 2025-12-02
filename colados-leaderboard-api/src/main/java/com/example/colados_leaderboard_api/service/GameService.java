package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.*;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.entity.GameResult;
import com.example.colados_leaderboard_api.enums.GameResultsStatus;
import com.example.colados_leaderboard_api.enums.ImageProcessingStatus;
import com.example.colados_leaderboard_api.enums.GameResultsInputMethod;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import com.example.colados_leaderboard_api.event.GameResultsCreatedFromProcessedMsg;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.exceptions.IllegalGameStateException;
import com.example.colados_leaderboard_api.exceptions.IncompleteGameResultsException;
import com.example.colados_leaderboard_api.exceptions.InvalidDataInGameResultsException;
import com.example.colados_leaderboard_api.mapper.GameMapper;
import com.example.colados_leaderboard_api.mapper.GameResultMapper;
import com.example.colados_leaderboard_api.mapper.ImageProcessedMsgMapper;
import com.example.colados_leaderboard_api.mapper.UpdateGameResultDtoMapper;
import com.example.colados_leaderboard_api.producer.MessageProducer;
import com.example.colados_leaderboard_api.repository.GameRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class GameService {

    private final FileService fileService;
    private final ChampionshipService championshipService;
    private final GameRepository gameRepository;
    private final MessageProducer messageProducer;
    private final ApplicationEventPublisher publisher;
    private final ImageProcessedMsgMapper imageProcessedMsgMapper;
    private final PlayerService playerService;
    private final UpdateGameResultDtoMapper updateGameResultDtoMapper;

    public GameService(FileService fileService, ChampionshipService championshipService, GameRepository gameRepository, MessageProducer messageProducer, ApplicationEventPublisher publisher, ImageProcessedMsgMapper imageProcessedMsgMapper, PlayerService playerService, UpdateGameResultDtoMapper updateGameResultDtoMapper) {
        this.fileService = fileService;
        this.championshipService = championshipService;
        this.gameRepository = gameRepository;
        this.messageProducer = messageProducer;
        this.publisher = publisher;
        this.imageProcessedMsgMapper = imageProcessedMsgMapper;
        this.playerService = playerService;
        this.updateGameResultDtoMapper = updateGameResultDtoMapper;
    }

    public void registerGame(RegisterGameDto registerGameDto, MultipartFile file) throws Exception {
        // Validate championship exists
        Championship championship = championshipService.getById(registerGameDto.getChampionshipId());

        // Register game played
        Game game = new Game();
        game.setChampionship(championship);
        game.setPlayedAt(registerGameDto.getPlayedAt() != null ? registerGameDto.getPlayedAt() : Instant.now());
        game.setStatusForEdition(StatusForEdition.OPEN);
        if (registerGameDto.getGameResults() != null && !registerGameDto.getGameResults().isEmpty()) {
            game.setGameResults(updateGameResultDtoMapper.toGameResultList(registerGameDto.getGameResults(), game));
            game.setGameResultsInputMethod(GameResultsInputMethod.MANUAL);
        }
        this.gameRepository.save(game);

        // Handle file if present
        if (file != null) {
            processGameImage(game.getId(), file, registerGameDto.isProcessImage());
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

    public void updateGameResultsStatus(Integer id, PatchGameResultsStatus patchGameResultsStatus) throws EntityNotFound, IncompleteGameResultsException, InvalidDataInGameResultsException, IllegalGameStateException {
        Game game = getGameById(id);

        // If game is closed, do not allow updates
        if (game.getStatusForEdition() == StatusForEdition.CLOSED) {
            throw new IllegalGameStateException("Cannot update game results status for a closed game.");
        }

        // Validate game results before accepting
        if (patchGameResultsStatus.getGameResultsStatus() == GameResultsStatus.ACCEPTED) {
            validateGameResultsForAcceptance(game);
        }
        game.setGameResultsStatus(patchGameResultsStatus.getGameResultsStatus());
        this.gameRepository.save(game);
    }

    private static void validateGameResultsForAcceptance(Game game) throws IncompleteGameResultsException, InvalidDataInGameResultsException {
        verifyGameResultsAreComplete(game.getGameResults());
        verifyGameResultsHasUniqueUserPlayer(game.getGameResults());
    }

    private static void verifyGameResultsAreComplete(List<GameResult> gameResults) throws IncompleteGameResultsException {
        for (GameResult result : gameResults) {
            if (result.getGame() == null || result.getPlayer() == null || result.getPosition() == null || result.getScore() == null || result.getCharacterName() == null) {
                throw new IncompleteGameResultsException("Incomplete game result found.");
            }
        }
    }

    private static void verifyGameResultsHasUniqueUserPlayer(List<GameResult> gameResults) throws InvalidDataInGameResultsException {
        Set<Integer> userIds = new HashSet<>();
        for (GameResult result : gameResults) {
            if (!userIds.add(result.getPlayer().getUser().getId())) {
                throw new InvalidDataInGameResultsException("Duplicate app-user/player found in game results.");
            }
        }
    }

    public void updateGame(Integer id, UpdateGameDto updateGameDto, MultipartFile file) throws Exception {
        Game game = this.getGameById(id);

        // If game is closed, do not allow updates
        if (game.getStatusForEdition() == StatusForEdition.CLOSED) {
            throw new IllegalGameStateException("Cannot update game results status for a closed game.");
        }

        // If ImageProcessingStatus is SUBMITTED, do not allow updates
        if (game.getImageProcessingStatus() == ImageProcessingStatus.SUBMITTED) {
            throw new IllegalGameStateException("Cannot update game results while image processing is not finished.");
        }

        // Validate championship exists
        Championship championship = championshipService.getById(updateGameDto.getChampionshipId());

        // Update basic game info
        game.setChampionship(championship);
        game.setPlayedAt(updateGameDto.getPlayedAt());

        // Update game results
        if (updateGameDto.getGameResults() != null && !updateGameDto.getGameResults().isEmpty()) {
            game.getGameResults().clear();
            game.getGameResults().addAll(updateGameResultDtoMapper.toGameResultList(updateGameDto.getGameResults(), game));
            game.setGameResultsInputMethod(GameResultsInputMethod.MANUAL);
        }
        this.gameRepository.save(game);

        // Handle file if present
        if (file != null) {
            processGameImage(game.getId(), file, updateGameDto.isProcessImage());
        }
    }

    private void processGameImage(Integer id, MultipartFile file, boolean processImage) throws Exception {
        String gameImageFileName = storeGameImage(id, file);
        if (processImage) {
            requestGameImageProcessing(gameImageFileName);
        }
    }

    private String storeGameImage(Integer id, MultipartFile file) throws Exception {
        Game game = this.getGameById(id);

        try {
            String newFileName = this.fileService.uploadFileToStorage(file);

            game.setScoreboardImageName(newFileName);
            this.gameRepository.save(game);

            return newFileName;
        } catch (IOException e) {
            throw new Exception("Failed to handle file: " + e.getMessage());
        }
    }

    private void requestGameImageProcessing(String gameImageFileName) throws Exception {
        try {
            this.messageProducer.sendMessage(new ImageSubmittedMsg(gameImageFileName));
        } catch (AmqpException e) {
            throw new Exception("Failed to send message to queue: " + e.getMessage());
        }
    }

    public byte[] getGameImage(Integer id) throws EntityNotFound {
        Game game = this.getGameById(id);
        if (game.getScoreboardImageName() == null) {
            throw new EntityNotFound("Game image not found for game ID: " + id);
        }
        return this.fileService.getFileFromStorage(game.getScoreboardImageName());
    }

    public void updateStatusForEdition(Integer id, PatchStatusForEdition patchStatusForEdition) throws EntityNotFound, IllegalGameStateException {
        Game game = this.getGameById(id);

        if (patchStatusForEdition.getStatusForEdition() == StatusForEdition.CLOSED && game.getGameResultsStatus() != GameResultsStatus.ACCEPTED) {
            throw new IllegalGameStateException("Cannot close the game edition when game results are not accepted.");
        }

        game.setStatusForEdition(patchStatusForEdition.getStatusForEdition());
        this.gameRepository.save(game);
    }
}
