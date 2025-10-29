package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.ChampionshipDto;
import com.example.colados_leaderboard_api.dto.CreateChampionshipDto;
import com.example.colados_leaderboard_api.exceptions.ChampionshipNameAlreadyExists;
import com.example.colados_leaderboard_api.service.ChampionshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/championships")
public class ChampionshipController {

    private final ChampionshipService championshipService;

    public ChampionshipController(ChampionshipService championshipService) {
        this.championshipService = championshipService;
    }

    @PostMapping()
    public ResponseEntity<ChampionshipDto> createChampionship(@Valid @RequestBody CreateChampionshipDto createChampionshipDto) throws ChampionshipNameAlreadyExists {
        return new ResponseEntity<>(
                championshipService.createChampionship(createChampionshipDto),
                HttpStatus.CREATED
        );
    }

}
