package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.dto.PlayerDto;
import com.example.colados_leaderboard_api.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class PlayerMapper {

    private PlayerMapper() {
        // Prevent instantiation
    }

    public PlayerDto toDto(Player player) {
        if (player == null) return null;
        PlayerDto dto = new PlayerDto();
        dto.setCharacterName(player.getCharacterName());
        dto.setCreatedAt(player.getCreatedAt());
        return dto;
    }

    public static Iterable<PlayerDto> toDtoList(Iterable<Player> players) {
        if (players == null) return null;
        List<PlayerDto> dtoList = new ArrayList<>();
        for (Player player : players) {
            dtoList.add(new PlayerMapper().toDto(player));
        }
        return dtoList;
    }
}
