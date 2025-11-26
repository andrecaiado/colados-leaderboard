package com.example.colados_leaderboard_api.repository;

import com.example.colados_leaderboard_api.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    @Query("SELECT p FROM Player p " +
            "WHERE p.characterName = :character " +
            "AND p.createdAt <= :createdAt " +
            "ORDER BY p.createdAt DESC " +
            "LIMIT 1")
    Optional<Player> findPlayerByCharacterAtOrBeforeDate(String character, Instant createdAt);

    @Query("SELECT p FROM Player p " +
            "WHERE p.user.id = :userId " +
            "AND p.createdAt <= :createdAt " +
            "ORDER BY p.createdAt DESC " +
            "LIMIT 1")
    Optional<Player> findPlayerByUserIdAtOrBeforeDate(Integer userId, Instant createdAt);
}
