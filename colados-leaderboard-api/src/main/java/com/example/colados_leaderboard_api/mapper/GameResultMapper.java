package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.dto.GameResultDto;
import com.example.colados_leaderboard_api.entity.GameResult;

import java.util.ArrayList;
import java.util.List;

public final class GameResultMapper {

    private GameResultMapper() {
        // Prevent instantiation
    }

    public static GameResultDto toDto(GameResult gameResult) {
        if (gameResult == null) return null;
        GameResultDto dto = new GameResultDto();
        dto.setId(gameResult.getId());
        if (gameResult.getPlayer() != null) {
            dto.setPlayerId(gameResult.getPlayer().getId());
            dto.setPlayerName(gameResult.getPlayer().getId().toString()); // TODO: Adjust when Player as a User entity
        }
        dto.setPosition(gameResult.getPosition());
        dto.setCharacterName(gameResult.getCharacterName());
        dto.setScore(gameResult.getScore());
        dto.setMaxScoreAchieved(gameResult.getMaxScoreAchieved());
        return dto;
    }

    public static Iterable<GameResultDto> toDtoList(Iterable<GameResult> gameResults) {
        if (gameResults == null) return null;
        List<GameResultDto> dtoList = new ArrayList<>();
        for (GameResult gameResult : gameResults) {
            dtoList.add(toDto(gameResult));
        }
        return dtoList;
    }
}
