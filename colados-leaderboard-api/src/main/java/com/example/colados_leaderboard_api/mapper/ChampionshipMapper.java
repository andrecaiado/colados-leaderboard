package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.dto.ChampionshipDto;
import com.example.colados_leaderboard_api.entity.Championship;

import java.util.ArrayList;
import java.util.List;

public final class ChampionshipMapper {

    private ChampionshipMapper() {
        // Prevent instantiation
    }

    public static ChampionshipDto toDto(Championship championship) {
        if (championship == null) return null;
        ChampionshipDto dto = new ChampionshipDto();
        dto.setId(championship.getId());
        dto.setName(championship.getName());
        dto.setDescription(championship.getDescription());
        return dto;
    }

    public static Iterable<ChampionshipDto> toDtoList(Iterable<Championship> championships) {
        if (championships == null) return null;
        List<ChampionshipDto> dtoList = new ArrayList<>();
        for (Championship championship : championships) {
            dtoList.add(toDto(championship));
        }
        return dtoList;
    }
}
