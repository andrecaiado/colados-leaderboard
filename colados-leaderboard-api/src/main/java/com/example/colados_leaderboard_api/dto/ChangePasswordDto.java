package com.example.colados_leaderboard_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChangePasswordDto {
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmNewPassword;

    // Match newPassword and confirmNewPassword validation

}
