package com.example.colados_leaderboard_api.repository;

import com.example.colados_leaderboard_api.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findByScoreboardImageName(String imageName);
}
