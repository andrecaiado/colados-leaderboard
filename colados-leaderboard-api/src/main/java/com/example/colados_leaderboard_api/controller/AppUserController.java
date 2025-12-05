package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.AppUserDto;
import com.example.colados_leaderboard_api.dto.ChangePasswordDto;
import com.example.colados_leaderboard_api.dto.UpdateProfileDto;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/app-users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PatchMapping("/password")
    public ResponseEntity<AppUserDto> updatePassword(
            @RequestBody ChangePasswordDto changePasswordDto,
            @AuthenticationPrincipal UserDetails userDetails) throws EntityNotFound {
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        appUserService.updatePassword(userDetails.getUsername(), changePasswordDto);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody @Valid UpdateProfileDto updateProfileDto,
            @AuthenticationPrincipal UserDetails userDetails) throws EntityNotFound {

        appUserService.updateProfile(userDetails.getUsername(), updateProfileDto);

        return ResponseEntity.noContent().build();
    }
}
