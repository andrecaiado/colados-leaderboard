package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.ChampionshipDto;
import com.example.colados_leaderboard_api.dto.CreateChampionshipDto;
import com.example.colados_leaderboard_api.exceptions.CustomDataIntegrityViolationException;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.service.ChampionshipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Championships", description = "Operations for managing championships")
@PreAuthorize("hasAuthority('ADMIN')")
@RestController
@RequestMapping("/api/v1/championships")
public class ChampionshipController {

    private final ChampionshipService championshipService;

    public ChampionshipController(ChampionshipService championshipService) {
        this.championshipService = championshipService;
    }

    @PostMapping()
    public ResponseEntity<ChampionshipDto> createChampionship(@Valid @RequestBody CreateChampionshipDto createChampionshipDto) throws CustomDataIntegrityViolationException {
        return new ResponseEntity<>(
                championshipService.createChampionship(createChampionshipDto),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateChampionship(@PathVariable Integer id, @Valid @RequestBody CreateChampionshipDto updateChampionshipDto) throws EntityNotFound, CustomDataIntegrityViolationException {
        championshipService.updateChampionship(id, updateChampionshipDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChampionship(@PathVariable Integer id, @RequestParam(name = "force", defaultValue = "false") boolean force) throws Exception {
        championshipService.deleteChampionship(id, force);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChampionshipDto> getChampionshipById(@PathVariable Integer id) throws EntityNotFound {
        ChampionshipDto championshipDto = championshipService.getChampionshipById(id);
        return ResponseEntity.ok(championshipDto);
    }

    @GetMapping()
    public ResponseEntity<Iterable<ChampionshipDto>> getAllChampionships() {
        Iterable<ChampionshipDto> championships = championshipService.getAllChampionships();
        return ResponseEntity.ok(championships);
    }
}
