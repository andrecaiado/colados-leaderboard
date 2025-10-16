package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.configuration.AppConstants;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.entity.GameResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ImageProcessedMsgMapper {

    private final AppConstants appConstants;

    public ImageProcessedMsgMapper(AppConstants appConstants) {
        this.appConstants = appConstants;
    }

    public List<GameResult> mapToGameResults(List<Map<String, Object>> gameResults, Game game) {
        return gameResults.stream()
                .map(result -> new GameResult(
                        null,
                        game,
                        null,
                        (Integer) result.get("position"),
                        (String) result.get("name"),
                        (Integer) result.get("score"),
                        result.get("score") != null && (Integer) result.get("score") >= appConstants.getGameMaxScore()
                ))
                .toList();

    }
}
