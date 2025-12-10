package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.service.CharacterNamesProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Characters", description = "Operations for retrieving character names")
@RestController
@RequestMapping("/api/v1/characters")
public class CharacterController {

    private final CharacterNamesProvider characterNamesProvider;

    public CharacterController(CharacterNamesProvider characterNamesProvider) {
        this.characterNamesProvider = characterNamesProvider;
    }

    @PreAuthorize("hasAnyAuthority('VIEWER', 'EDITOR')")
    @GetMapping("/names")
    public List<String> getCharacterNames() {
        return characterNamesProvider.getCharacterNames();
    }
}
