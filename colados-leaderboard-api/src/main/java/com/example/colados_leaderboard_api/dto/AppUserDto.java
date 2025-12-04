package com.example.colados_leaderboard_api.dto;

import com.example.colados_leaderboard_api.enums.AppUserRoles;
import com.example.colados_leaderboard_api.enums.AuthProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AppUserDto {
    private Integer id;
    private String username;
    private String email;
    private List<AppUserRoles> roles;
    private AuthProvider authProvider;
}
