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
public class SuperAdminBootstrap implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${superadmin.email}")
    private String superAdminEmail;

    @Value("${superadmin.password}")
    private String superAdminPassword;

    public SuperAdminBootstrap(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) {
        if (appUserRepository.findByEmail(superAdminEmail).isEmpty()) {
            var superAdmin = new AppUser();
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setRoles(List.of(AppUserRoles.SUPER_ADMIN));
            superAdmin.setAuthProvider(AuthProvider.LOCAL);

            appUserRepository.save(superAdmin);
            System.out.println("Super admin user created with email: " + superAdminEmail);
        } else {
            System.out.println("Super admin user already exists.");
        }
    }
}
