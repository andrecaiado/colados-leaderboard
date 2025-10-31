package com.example.colados_leaderboard_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GameResultDto {
    private Integer id;
    private Integer playerId;
    private String playerName;
    private Integer position;
    private String characterName;
    private Integer score;
    private Boolean maxScoreAchieved;
}
