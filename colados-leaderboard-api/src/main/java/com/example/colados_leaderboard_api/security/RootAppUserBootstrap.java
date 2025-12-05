package com.example.colados_leaderboard_api.security;

import com.example.colados_leaderboard_api.entity.AppUser;
import com.example.colados_leaderboard_api.enums.AppUserRoles;
import com.example.colados_leaderboard_api.enums.AuthProvider;
import com.example.colados_leaderboard_api.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RootAppUserBootstrap implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${rootappuser.email}")
    private String rootAppUserEmail;

    @Value("${rootappuser.password}")
    private String rootAppUserPassword;

    public RootAppUserBootstrap(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) {
        if (appUserRepository.findByEmail(rootAppUserEmail).isEmpty()) {
            var rootAppUser = new AppUser();
            rootAppUser.setUsername("root");
            rootAppUser.setEmail(rootAppUserEmail);
            rootAppUser.setPassword(passwordEncoder.encode(rootAppUserPassword));
            rootAppUser.setRoles(List.of(AppUserRoles.ADMIN));
            rootAppUser.setAuthProvider(AuthProvider.LOCAL);
            rootAppUser.setRoot(true);

            appUserRepository.save(rootAppUser);
            System.out.println("Root app user created with email: " + rootAppUserEmail);
        } else {
            System.out.println("Root app user already exists.");
        }
    }
}
