package com.example.colados_leaderboard_api.mapper;

import com.example.colados_leaderboard_api.dto.AppUserDto;
import com.example.colados_leaderboard_api.entity.AppUser;

import java.util.ArrayList;
import java.util.List;

public final class AppUserMapper {

    private AppUserMapper() {
        // Prevent instantiation
    }

    public static AppUserDto toDto(AppUser user) {
        if (user == null) return null;
        AppUserDto dto = new AppUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setAuthProvider(user.getAuthProvider());
        return dto;
    }

    public static Iterable<AppUserDto> toDtoList(Iterable<AppUser> users) {
        if (users == null) return null;
        List<AppUserDto> dtoList = new ArrayList<>();
        for (AppUser user : users) {
            dtoList.add(toDto(user));
        }
        return dtoList;
    }
}
