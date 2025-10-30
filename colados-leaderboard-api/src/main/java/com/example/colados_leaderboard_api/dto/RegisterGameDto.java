package com.example.colados_leaderboard_api.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
public class RegisterGameDto {
    @Nullable
    private Integer championshipId;

    private Instant playedAt = Instant.now();
}
