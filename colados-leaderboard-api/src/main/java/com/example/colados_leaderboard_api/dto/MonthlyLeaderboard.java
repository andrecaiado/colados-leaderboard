package com.example.colados_leaderboard_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyLeaderboard {
    private Integer rank;
    private Integer playerId;
    private Integer countPosition;
    private Integer position;
    private Integer totalScore;
    private Integer maxScoreAchievedCount;
}
