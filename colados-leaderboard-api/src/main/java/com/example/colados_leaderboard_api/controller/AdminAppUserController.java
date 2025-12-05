package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.AppUserDto;
import com.example.colados_leaderboard_api.dto.RegisterExternalAppUserDto;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/app-users")
public class AdminAppUserController {

    private final AppUserService appUserService;

    public AdminAppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping()
    public ResponseEntity<Iterable<AppUserDto>> getAllAppUsers() {
        return ResponseEntity.ok(appUserService.getAll());
    }

    @PostMapping()
    public ResponseEntity<AppUserDto> registerExternal(@RequestBody @Valid RegisterExternalAppUserDto registerExternalAppUserDto) {
        var createdUser = appUserService.registerExternal(registerExternalAppUserDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{appUserId}")
    public ResponseEntity<AppUserDto> updateExternalUserRoles(
            @PathVariable Integer appUserId,
            @RequestBody @Valid RegisterExternalAppUserDto registerExternalAppUserDto) throws EntityNotFound {
        appUserService.updateExternalUser(appUserId, registerExternalAppUserDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{appUserId}")
    public ResponseEntity<Void> deleteExternal(@PathVariable Integer appUserId) throws EntityNotFound {
        appUserService.deleteExternal(appUserId);

        return ResponseEntity.noContent().build();
    }
}
