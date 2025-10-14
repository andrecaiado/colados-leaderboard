package com.example.colados_leaderboard_api.repository;

import com.example.colados_leaderboard_api.entity.Championship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChampionshipRepository extends JpaRepository<Championship, Integer> {

}
