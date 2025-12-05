package com.example.colados_leaderboard_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateProfileDto {
    @NotNull
    private String username;
    @NotNull
    private String characterName;
}
