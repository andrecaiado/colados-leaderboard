package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.AppUserDto;
import com.example.colados_leaderboard_api.dto.ChangePasswordDto;
import com.example.colados_leaderboard_api.dto.RegisterExternalAppUserDto;
import com.example.colados_leaderboard_api.dto.UpdateProfileDto;
import com.example.colados_leaderboard_api.entity.AppUser;
import com.example.colados_leaderboard_api.entity.Player;
import com.example.colados_leaderboard_api.enums.AuthProvider;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.mapper.AppUserMapper;
import com.example.colados_leaderboard_api.repository.AppUserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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
        return appUserRepository.findById(userId).orElseThrow(() -> new EntityNotFound("User not found with ID: " + userId));
    }

    private AppUser getByEmail(String email) throws EntityNotFound {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User not found with email: " + email));
    }

    public AppUserDto registerExternal(RegisterExternalAppUserDto registerExternalAppUserDto) {
        validateAppUserDetailsForRegistrationAndUpdate(registerExternalAppUserDto, null);

        var appUser = new AppUser();
        appUser.setUsername(registerExternalAppUserDto.getUsername());
        appUser.setEmail(registerExternalAppUserDto.getEmail());
        appUser.setRoles(registerExternalAppUserDto.getRoles());
        appUser.setAuthProvider(AuthProvider.EXTERNAL);

        return AppUserMapper.toDto(appUserRepository.save(appUser));
    }

    public void updatePassword(String email, ChangePasswordDto changePasswordDto) throws EntityNotFound {
        var appUser = getByEmail(email);

        // External users cannot change passwords
        if (AuthProvider.EXTERNAL.equals(appUser.getAuthProvider())) {
            throw new IllegalArgumentException("External users cannot change passwords");
        }

        // Verify the old password and hash the new password
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), appUser.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        appUser.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        appUserRepository.save(appUser);
    }

    public void deleteExternal(Integer appUserId) throws EntityNotFound {
        AppUser appUser = getById(appUserId);

        if (appUser.isRoot()) {
            throw new EntityNotFound("User not found with ID: " + appUserId);
        }

        appUserRepository.delete(appUser);
    }

    public void updateExternalUser(Integer appUserId, RegisterExternalAppUserDto registerExternalAppUserDto) throws EntityNotFound {
        validateAppUserDetailsForRegistrationAndUpdate(registerExternalAppUserDto, appUserId);

        AppUser appUser = getById(appUserId);

        appUser.setUsername(registerExternalAppUserDto.getUsername());
        appUser.setEmail(registerExternalAppUserDto.getEmail());
        appUser.setRoles(registerExternalAppUserDto.getRoles());

        appUserRepository.save(appUser);
    }

    private void validateAppUserDetailsForRegistrationAndUpdate(RegisterExternalAppUserDto dto, Integer appUserId) {
        verifyUsernameNotUsedByAnotherUser(dto.getUsername(), appUserId);
        verifyEmailNotUsedByAnotherUser(dto.getEmail(), appUserId);
    }

    private void verifyUsernameNotUsedByAnotherUser(String username, Integer appUserId) {
        appUserRepository.findByUsername(username).ifPresent(user -> {
            if (appUserId == null || !appUserId.equals(user.getId())) {
                throw new IllegalArgumentException("Username " + username + " is already in use by another user.");
            }
        });
    }

    private void verifyEmailNotUsedByAnotherUser(String email, Integer appUserId) {
        appUserRepository.findByEmail(email).ifPresent(user -> {
            if (appUserId == null || !appUserId.equals(user.getId())) {
                throw new IllegalArgumentException("Email " + email + " is already in use by another user.");
            }
        });
    }

    public void updateProfile(String email, UpdateProfileDto updateProfileDto) throws EntityNotFound {
        AppUser user = getByEmail(email);

        user.setUsername(updateProfileDto.getUsername());

        Player mostRecentPlayer = user.getPlayers().stream()
                .max(Comparator.comparing(Player::getCreatedAt))
                .orElse(null);

        if (mostRecentPlayer == null || !updateProfileDto.getCharacterName().equals(mostRecentPlayer.getCharacterName())) {
            Player newPlayer = new Player();
            newPlayer.setCharacterName(updateProfileDto.getCharacterName());
            newPlayer.setCreatedAt(java.time.Instant.now());
            newPlayer.setUser(user);

            List<Player> players = user.getPlayers();
            players.add(newPlayer);
            user.setPlayers(players);
        }
        user.getPlayers().forEach(p -> System.out.println(p.getCharacterName() + " " + p.getCreatedAt()));

        appUserRepository.save(user);
    }
}
