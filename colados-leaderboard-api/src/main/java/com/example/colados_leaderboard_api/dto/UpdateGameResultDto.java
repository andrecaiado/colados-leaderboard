package com.example.colados_leaderboard_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateGameResultDto {
    private Integer userId;
    private Integer playerId;
    private Integer position;
    private Integer score;
}
