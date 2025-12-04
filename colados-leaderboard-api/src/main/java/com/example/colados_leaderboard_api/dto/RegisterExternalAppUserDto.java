package com.example.colados_leaderboard_api.dto;

import com.example.colados_leaderboard_api.enums.AppUserRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class RegisterExternalAppUserDto {
    @NotNull
    private String username;

    @NotNull
    @Email
    private String email;

    @NotEmpty
    private List<AppUserRoles> roles;
}
