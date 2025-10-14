package com.example.colados_leaderboard_api.entity;

import jakarta.persistence.*;

import javax.validation.constraints.NotNull;

@Entity
public class Championship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(unique = true)
    private String name;
}
