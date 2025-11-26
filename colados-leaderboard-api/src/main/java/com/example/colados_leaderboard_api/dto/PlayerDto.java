package com.example.colados_leaderboard_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class PlayerDto {
    private String characterName;
    private Instant createdAt;
}
