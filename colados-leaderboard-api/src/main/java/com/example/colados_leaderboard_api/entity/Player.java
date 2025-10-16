package com.example.colados_leaderboard_api.entity;

import jakarta.persistence.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerCharacter> playerCharacters;
}
