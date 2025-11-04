package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.MonthlyLeaderboard;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.repository.LeaderboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final ChampionshipService championshipService;

    public LeaderboardService(LeaderboardRepository leaderboardRepository, ChampionshipService championshipService) {
        this.leaderboardRepository = leaderboardRepository;
        this.championshipService = championshipService;
    }

    public List<MonthlyLeaderboard> getMonthlyLeaderboard(Integer championshipId, Integer month, Integer year) throws EntityNotFound {
        championshipService.getById(championshipId); // Ensure championship exists

        var now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);
        month = month != null ? month : now.getMonthValue();
        year = year != null ? year : now.getYear();

        return leaderboardRepository.getMonthlyLeaderboard(championshipId, month, year);
    }
}
