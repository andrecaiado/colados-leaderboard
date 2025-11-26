package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.PlayerDto;
import com.example.colados_leaderboard_api.entity.AppUser;
import com.example.colados_leaderboard_api.entity.Player;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.mapper.PlayerMapper;
import com.example.colados_leaderboard_api.repository.PlayerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final AppUserService appUserService;

    public PlayerService(PlayerRepository playerRepository, AppUserService appUserService) {
        this.playerRepository = playerRepository;
        this.appUserService = appUserService;
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

    public Iterable<PlayerDto> getByUser(Integer userId) throws EntityNotFound {
        AppUser user = appUserService.getById(userId);
        return PlayerMapper.toDtoList(playerRepository.findByUser(user, Sort.by("createdAt")));
    }
}
