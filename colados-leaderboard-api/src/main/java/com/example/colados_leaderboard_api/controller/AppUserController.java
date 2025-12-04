package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.AppUserDto;
import com.example.colados_leaderboard_api.dto.RegisterExternalAppUserDto;
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
}
