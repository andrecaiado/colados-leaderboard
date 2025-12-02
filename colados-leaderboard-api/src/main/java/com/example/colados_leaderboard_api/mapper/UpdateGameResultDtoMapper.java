package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.configuration.AppConstants;
import com.example.colados_leaderboard_api.dto.UpdateGameResultDto;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.entity.GameResult;
import com.example.colados_leaderboard_api.entity.Player;
import com.example.colados_leaderboard_api.service.PlayerService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateGameResultDtoMapper {

    private final PlayerService playerService;
    private final AppConstants appConstants;

    public UpdateGameResultDtoMapper(PlayerService playerService, AppConstants appConstants) {
        this.playerService = playerService;
        this.appConstants = appConstants;
    }

    public GameResult toGameResult(UpdateGameResultDto resultDto, Game game) {
        Player player = null;
        if (resultDto.getPlayerId() == null && resultDto.getUserId() != null) {
            // If playerId is not provided, but userId is, try to fetch player by userId and playedAt
            player = playerService.getByUserId(
                    resultDto.getUserId(),
                    game.getPlayedAt()
            );
        } else if (resultDto.getPlayerId() != null) {
            // If playerId is provided, fetch player by playerId
            player = playerService.getById(resultDto.getPlayerId());
        }
        GameResult gameResult = new GameResult();
        gameResult.setGame(game);
        gameResult.setPlayer(player);
        gameResult.setCharacterName(
                player != null ? player.getCharacterName() : null
        );
        gameResult.setPosition(resultDto.getPosition());
        gameResult.setScore(resultDto.getScore());
        gameResult.setMaxScoreAchieved(resultDto.getScore() != null && resultDto.getScore() >= appConstants.getGameMaxScore());

        return gameResult;
    }

    public List<GameResult> toGameResultList(List<UpdateGameResultDto> resultDtos, Game game) {
        if (resultDtos == null) return null;
        List<GameResult> gameResults = new ArrayList<>();
        for (UpdateGameResultDto dto : resultDtos) {
            gameResults.add(toGameResult(dto, game));
        }
        return gameResults;
    }
}
