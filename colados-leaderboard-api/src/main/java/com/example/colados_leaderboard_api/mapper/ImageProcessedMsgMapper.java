package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.constants.BusinessProperties;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.entity.GameResult;

import java.util.List;
import java.util.Map;

public final class ImageProcessedMsgMapper {

    private static final BusinessProperties businessProperties = new BusinessProperties();

    private ImageProcessedMsgMapper() {
        // Prevent instantiation
    }

    public static List<GameResult> mapToGameResults(List<Map<String, Object>> gameResults, Game game) {
        return gameResults.stream()
                .map(result -> new GameResult(
                        null,
                        game,
                        null,
                        (Integer) result.get("position"),
                        (String) result.get("name"),
                        (Integer) result.get("score"),
                        result.get("score") != null && (Integer) result.get("score") >= businessProperties.getGameMaxScore()
                ))
                .toList();

    }
}
