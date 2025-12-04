package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.AppUserDto;
import com.example.colados_leaderboard_api.dto.ChangePasswordDto;
import com.example.colados_leaderboard_api.dto.RegisterExternalAppUserDto;
import com.example.colados_leaderboard_api.entity.AppUser;
import com.example.colados_leaderboard_api.enums.AuthProvider;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.mapper.AppUserMapper;
import com.example.colados_leaderboard_api.repository.AppUserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Iterable<AppUserDto> getAll() {
        return AppUserMapper.toDtoList(appUserRepository.findAll(Sort.by("username")));
    }

    public AppUser getById(Integer userId) throws EntityNotFound {
        return appUserRepository.findById(userId).orElseThrow(() -> new EntityNotFound("AppUser not found with ID: " + userId));
    }

    public AppUserDto registerExternal(RegisterExternalAppUserDto registerExternalAppUserDto) {
        appUserRepository.findByEmail(registerExternalAppUserDto.getEmail()).ifPresent(user -> {
            throw new IllegalArgumentException("User with email " + registerExternalAppUserDto.getEmail() + " already exists.");
        });

        var appUser = new AppUser();
        appUser.setUsername(registerExternalAppUserDto.getUsername());
        appUser.setEmail(registerExternalAppUserDto.getEmail());
        appUser.setRoles(registerExternalAppUserDto.getRoles());
        appUser.setAuthProvider(AuthProvider.EXTERNAL);

        return AppUserMapper.toDto(appUserRepository.save(appUser));
    }

    public void updatePassword(Integer appUserId, ChangePasswordDto changePasswordDto) throws EntityNotFound {
        var appUser = getById(appUserId);

        // Verify the old password and hash the new password
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), appUser.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        appUser.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        appUserRepository.save(appUser);
    }
}
