package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.MonthlyLeaderboard;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PreAuthorize("hasAnyAuthority('VIEWER', 'EDITOR')")
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyLeaderboard>> getMonthlyLeaderboard(@RequestParam Integer championshipId,
                                                                         @RequestParam(required = false) Integer month,
                                                                         @RequestParam(required = false) Integer year) throws EntityNotFound {
        return ResponseEntity.ok(leaderboardService.getMonthlyLeaderboard(championshipId, month, year));
    }
}
