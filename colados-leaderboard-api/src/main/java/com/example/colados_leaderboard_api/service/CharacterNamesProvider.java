package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.controller.CharacterController;
import com.example.colados_leaderboard_api.dto.CharacterList;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Getter
@Component
public class CharacterNamesProvider {
    private List<String> characterNames;
    private CharacterList characterList;

    @PostConstruct
    public void init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/static/character-names.json")) {
            if (is == null) {
                throw new IllegalStateException("/static/character-names.json not found in classpath");
            }
            characterList = mapper.readValue(is, CharacterList.class);
            characterNames = characterList.getCharacters();
        }
    }
}
