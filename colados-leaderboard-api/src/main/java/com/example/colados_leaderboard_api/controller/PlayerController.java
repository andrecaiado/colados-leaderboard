package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.PlayerDto;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Iterable<PlayerDto>> getPlayersByUserId(@PathVariable Integer userId) throws EntityNotFound {
        return ResponseEntity.ok(playerService.getByUser(userId));
    }
}
