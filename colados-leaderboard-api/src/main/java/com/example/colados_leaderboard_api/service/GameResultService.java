package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.entity.GameResult;
import org.springframework.stereotype.Service;

@Service
public class GameResultService {
    private final PlayerService playerService;

    public GameResultService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setPlayerFromCharacterName(GameResult gameResult) {
        gameResult.setPlayer(
                playerService.getPlayerByCharacter(
                        gameResult.getCharacterName(),
                        gameResult.getGame().getPlayedAt()
                )
        );
    }
}
