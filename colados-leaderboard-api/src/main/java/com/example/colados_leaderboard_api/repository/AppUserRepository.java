package com.example.colados_leaderboard_api.repository;

import com.example.colados_leaderboard_api.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUsername(String username);
}
