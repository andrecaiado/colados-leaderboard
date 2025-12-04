package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.AppUserDto;
import com.example.colados_leaderboard_api.dto.ChangePasswordDto;
import com.example.colados_leaderboard_api.dto.RegisterExternalAppUserDto;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/app-users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping()
    public ResponseEntity<Iterable<AppUserDto>> getAllAppUsers() {
        return ResponseEntity.ok(appUserService.getAll());
    }

    @PostMapping("/register-external")
    public ResponseEntity<AppUserDto> registerExternal(@RequestBody @Valid RegisterExternalAppUserDto registerExternalAppUserDto) {
        var createdUser = appUserService.registerExternal(registerExternalAppUserDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PatchMapping("/{appUserId}/password")
    public ResponseEntity<AppUserDto> updatePassword(
            @PathVariable Integer appUserId,
            @RequestBody ChangePasswordDto changePasswordDto) throws EntityNotFound {
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }
        appUserService.updatePassword(appUserId, changePasswordDto);

        return ResponseEntity.noContent().build();
    }
}
