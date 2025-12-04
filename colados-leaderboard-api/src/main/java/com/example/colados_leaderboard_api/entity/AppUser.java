package com.example.colados_leaderboard_api.entity;

import com.example.colados_leaderboard_api.enums.AppUserRoles;
import com.example.colados_leaderboard_api.enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String username;

    private String password;

    @NotNull
    @Email
    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Player> players;

    @NotEmpty
    @ElementCollection(targetClass = AppUserRoles.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "app_user_roles", joinColumns = @JoinColumn(name = "app_user_id"))
    @Column(name = "roles")
    private List<AppUserRoles> roles;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;
}
