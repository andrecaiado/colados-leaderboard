package com.example.colados_leaderboard_api.entity;

import com.example.colados_leaderboard_api.enums.ImageProcessingStatus;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private StatusForEdition statusForEdition = StatusForEdition.OPEN;

    @Nullable
    private String scoreboardImageName;

    @Nullable
    @Enumerated(EnumType.STRING)
    private ImageProcessingStatus imageProcessingStatus = ImageProcessingStatus.NONE;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameResult> gameResults = new ArrayList<>();
}
