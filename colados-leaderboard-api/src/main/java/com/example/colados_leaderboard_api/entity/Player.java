package com.example.colados_leaderboard_api.entity;

import jakarta.persistence.*;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private String characterName;

    @NotNull
    private Instant createdAt;
}
