package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.*;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.exceptions.IllegalGameStateException;
import com.example.colados_leaderboard_api.exceptions.IncompleteGameResultsException;
import com.example.colados_leaderboard_api.exceptions.InvalidDataInGameResultsException;
import com.example.colados_leaderboard_api.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerGame(@RequestPart("game") RegisterGameDto registerGameDto, @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        if (file != null && file.getContentType() != null && !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        gameService.registerGame(registerGameDto, file);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('VIEWER', 'EDITOR')")
    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGame(@PathVariable Integer id) throws EntityNotFound {
        GameDto game = gameService.getGame(id);

        return ResponseEntity.ok(game);
    }

    @PreAuthorize("hasAnyAuthority('VIEWER', 'EDITOR')")
    @GetMapping()
    public ResponseEntity<Iterable<GameDto>> getAllGames() {
        Iterable<GameDto> games = gameService.getAllGames();

        return ResponseEntity.ok(games);
    }

    @PreAuthorize("hasAnyAuthority('VIEWER', 'EDITOR')")
    @GetMapping("/{id}/results")
    public ResponseEntity<Iterable<GameResultDto>> getGameResults(@PathVariable Integer id) throws EntityNotFound {
        Iterable<GameResultDto> results = gameService.getGameResults(id);

        return ResponseEntity.ok(results);
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @PatchMapping("/{id}/game-results-status")
    public ResponseEntity<Void> updateGameResultsStatus(@PathVariable Integer id, @Valid @RequestBody PatchGameResultsStatus patchGameResultsStatus) throws EntityNotFound, IncompleteGameResultsException, InvalidDataInGameResultsException, IllegalGameStateException {
        gameService.updateGameResultsStatus(id, patchGameResultsStatus);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGame(@PathVariable Integer id, @Valid @RequestPart("game") UpdateGameDto updateGameDto, @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        if (file != null && file.getContentType() != null && !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        gameService.updateGame(id, updateGameDto, file);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('VIEWER', 'EDITOR')")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getGameImage(@PathVariable Integer id) throws EntityNotFound {
        byte[] imageData = gameService.getGameImage(id);

        String mediaType = detectMediaType(imageData);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mediaType)).body(imageData);
    }

    private static String detectMediaType(byte[] data) {
        // Check for WebP header: "RIFF....WEBP"
        if (data.length >= 12 &&
                data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' &&
                data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') {
            return "image/webp";
        }
        try (InputStream is = new BufferedInputStream(new ByteArrayInputStream(data))) {
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @PatchMapping("/{id}/status-for-edition")
    public ResponseEntity<Void> updateStatusForEdition(@PathVariable Integer id, @Valid @RequestBody PatchStatusForEdition patchStatusForEdition) throws EntityNotFound, IllegalGameStateException {
        gameService.updateStatusForEdition(id, patchStatusForEdition);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
