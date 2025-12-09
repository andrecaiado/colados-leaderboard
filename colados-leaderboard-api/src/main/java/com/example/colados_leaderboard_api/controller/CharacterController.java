package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.service.CharacterNamesProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/characters")
public class CharacterController {

    private final CharacterNamesProvider characterNamesProvider;

    public CharacterController(CharacterNamesProvider characterNamesProvider) {
        this.characterNamesProvider = characterNamesProvider;
    }

    @GetMapping("/names")
    public List<String> getCharacterNames() {
        return characterNamesProvider.getCharacterNames();
    }
}
