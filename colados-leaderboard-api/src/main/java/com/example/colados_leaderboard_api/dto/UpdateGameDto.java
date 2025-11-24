package com.example.colados_leaderboard_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UpdateGameDto {
    @NotNull
    private Integer championshipId;

    @NotNull
    private Instant playedAt;

    @NotNull
    private List<UpdateGameResultDto> gameResults;
}
