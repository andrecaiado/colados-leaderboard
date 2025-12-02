package com.example.colados_leaderboard_api.dto;

import com.example.colados_leaderboard_api.enums.StatusForEdition;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PatchStatusForEdition {
    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusForEdition statusForEdition;
}
