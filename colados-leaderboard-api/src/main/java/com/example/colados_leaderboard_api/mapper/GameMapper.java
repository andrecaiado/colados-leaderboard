package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.dto.GameDto;
import com.example.colados_leaderboard_api.entity.Game;

import java.util.ArrayList;
import java.util.List;

public final class GameMapper {

    private GameMapper() {
        // Prevent instantiation
    }

    public static GameDto toDto(Game game) {
        if (game == null) return null;
        GameDto dto = new GameDto();
        dto.setId(game.getId());
        dto.setChampionshipId(game.getChampionship().getId());
        dto.setChampionshipName(game.getChampionship().getName());
        dto.setPlayedAt(game.getPlayedAt());
        dto.setStatusForEdition(game.getStatusForEdition());
        dto.setScoreboardImageName(game.getScoreboardImageName());
        dto.setImageProcessingStatus(game.getImageProcessingStatus());
        dto.setGameResultsStatus(game.getGameResultsStatus());
        dto.setGameResultsAcceptedBy(game.getGameResultsAcceptedBy());
        dto.setGameResultsInputMethod(game.getGameResultsInputMethod());
        return dto;
    }

    public static Iterable<GameDto> toDtoList(Iterable<Game> games) {
        if (games == null) return null;
        List<GameDto> dtoList = new ArrayList<>();
        for (Game game : games) {
            dtoList.add(toDto(game));
        }
        return dtoList;
    }
}
