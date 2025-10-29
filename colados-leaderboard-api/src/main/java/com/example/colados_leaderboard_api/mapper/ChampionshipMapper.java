package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.dto.ChampionshipDto;
import com.example.colados_leaderboard_api.entity.Championship;

public final class ChampionshipMapper {

    private ChampionshipMapper() {
        // Prevent instantiation
    }

    public static ChampionshipDto toDto(Championship championship) {
        if (championship == null) return null;
        ChampionshipDto dto = new ChampionshipDto();
        dto.setName(championship.getName());
        dto.setDescription(championship.getDescription());
        return dto;
    }
}
