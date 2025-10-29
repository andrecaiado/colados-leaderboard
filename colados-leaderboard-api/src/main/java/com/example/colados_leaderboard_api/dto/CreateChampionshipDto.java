package com.example.colados_leaderboard_api.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChampionshipDto {
    @NotBlank
    private String name;

    private String description;
}
