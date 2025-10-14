package com.example.colados_leaderboard_api.entity;

import com.example.colados_leaderboard_api.enums.StatusForEdition;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "championship_id", nullable = false)
    private Championship championship;

    @NotNull
    private Instant playedAt = Instant.now();

    @NotNull
    private StatusForEdition statusForEdition = StatusForEdition.OPEN;

    @Nullable
    private String scoreboardImageName;

    @Nullable
    private String imageProcessingResult;

}
