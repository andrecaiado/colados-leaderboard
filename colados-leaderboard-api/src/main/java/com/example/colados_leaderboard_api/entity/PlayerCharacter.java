package com.example.colados_leaderboard_api.entity;

import jakarta.persistence.*;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
public class PlayerCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String characterName;

    @NotNull
    private Instant createdAt = Instant.now();

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
}
