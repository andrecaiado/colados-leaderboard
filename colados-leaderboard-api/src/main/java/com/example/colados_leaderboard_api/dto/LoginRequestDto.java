package com.example.colados_leaderboard_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
