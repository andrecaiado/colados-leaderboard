package com.example.colados_leaderboard_api.repository;

import com.example.colados_leaderboard_api.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findByScoreboardImageName(String imageName);
}
