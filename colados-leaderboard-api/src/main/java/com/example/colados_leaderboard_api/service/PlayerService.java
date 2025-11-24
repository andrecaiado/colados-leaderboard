package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.entity.Player;
import com.example.colados_leaderboard_api.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player getByCharacter(String character, Instant createdAt) {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        return playerRepository.findPlayerByCharacterAtOrBeforeDate(character, createdAt)
                .orElse(null);
    }

    public Player getById(Integer playerId) {
        return playerRepository.findById(playerId).orElse(null);
    }

    public Player getByUserId(Integer userId, Instant createdAt) {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        return playerRepository.findPlayerByUserIdAtOrBeforeDate(userId, createdAt)
                .orElse(null);
    }
}
