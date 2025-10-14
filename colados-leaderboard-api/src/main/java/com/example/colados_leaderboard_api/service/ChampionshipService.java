package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.repository.ChampionshipRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChampionshipService {

    private final ChampionshipRepository championshipRepository;

    public ChampionshipService(ChampionshipRepository championshipRepository) {
        this.championshipRepository = championshipRepository;
    }

    public Optional<Championship> findById(Integer id) {
        return championshipRepository.findById(id);
    }
}
