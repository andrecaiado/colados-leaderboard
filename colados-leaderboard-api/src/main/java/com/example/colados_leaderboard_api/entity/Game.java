package com.example.colados_leaderboard_api.entity;

import com.example.colados_leaderboard_api.enums.GameResultsStatus;
import com.example.colados_leaderboard_api.enums.ImageProcessingStatus;
import com.example.colados_leaderboard_api.enums.GameResultsInputMethod;
import com.example.colados_leaderboard_api.enums.StatusForEdition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
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

    private String scoreboardImageName;

    @Enumerated(EnumType.STRING)
    private ImageProcessingStatus imageProcessingStatus = ImageProcessingStatus.NONE;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GameResult> gameResults = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GameResultsStatus gameResultsStatus = GameResultsStatus.PENDING_ACCEPTANCE;

    private Integer gameResultsAcceptedBy;

    @Enumerated(EnumType.STRING)
    private GameResultsInputMethod gameResultsInputMethod;
}
