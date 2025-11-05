package com.example.colados_leaderboard_api.dto;

import com.example.colados_leaderboard_api.enums.GameResultsStatus;
import com.example.colados_leaderboard_api.enums.ImageProcessingStatus;
import com.example.colados_leaderboard_api.enums.GameResultsInputMethod;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
public class GameDto {
    private Integer id;
    private Integer championshipId;
    private String championshipName;
    private Instant playedAt = Instant.now();
    private StatusForEdition statusForEdition;
    private String scoreboardImageName;
    private ImageProcessingStatus imageProcessingStatus;
    private GameResultsStatus gameResultsStatus;
    private Integer gameResultsAcceptedBy;
    private GameResultsInputMethod gameResultsInputMethod;
}
