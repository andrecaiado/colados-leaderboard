package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.*;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.exceptions.IncompleteGameResultsException;
import com.example.colados_leaderboard_api.exceptions.InvalidDataInGameResultsException;
import com.example.colados_leaderboard_api.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> registerGame(@RequestPart("game") RegisterGameDto registerGameDto, @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        if (file != null && file.getContentType() != null && !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        gameService.registerGame(registerGameDto, file);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGame(@PathVariable Integer id) throws EntityNotFound {
        GameDto game = gameService.getGame(id);
        return ResponseEntity.ok(game);
    }

    @GetMapping()
    public ResponseEntity<Iterable<GameDto>> getAllGames() {
        Iterable<GameDto> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<Iterable<GameResultDto>> getGameResults(@PathVariable Integer id) throws EntityNotFound {
        Iterable<GameResultDto> results = gameService.getGameResults(id);
        return ResponseEntity.ok(results);
    }

    @PatchMapping("/{id}/game-results-status")
    public ResponseEntity<Void> updateGameResultsStatus(@PathVariable Integer id, @Valid @RequestBody PatchGameResultsStatus patchGameResultsStatus) throws EntityNotFound, IncompleteGameResultsException, InvalidDataInGameResultsException {
        gameService.updateGameResultsStatus(id, patchGameResultsStatus);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGame(@PathVariable Integer id, @Valid @RequestPart("game") UpdateGameDto updateGameDto, @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        if (file != null && file.getContentType() != null && !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        gameService.updateGame(id, updateGameDto, file);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
