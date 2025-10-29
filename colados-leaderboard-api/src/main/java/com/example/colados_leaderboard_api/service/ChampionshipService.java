package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.ChampionshipDto;
import com.example.colados_leaderboard_api.dto.CreateChampionshipDto;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.exceptions.ChampionshipNameAlreadyExists;
import com.example.colados_leaderboard_api.mapper.ChampionshipMapper;
import com.example.colados_leaderboard_api.repository.ChampionshipRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChampionshipService {

    private final ChampionshipRepository championshipRepository;

    public ChampionshipService(ChampionshipRepository championshipRepository) {
        this.championshipRepository = championshipRepository;
    }

    public Optional<Championship> findById(Integer id) {
        return championshipRepository.findById(id);
    }

    private Optional<Championship> findByName(String name) {
        return championshipRepository.findByName(name);
    }

    public ChampionshipDto createChampionship(CreateChampionshipDto createChampionshipDto) throws ChampionshipNameAlreadyExists {
        Championship championship = new Championship();
        championship.setName(createChampionshipDto.getName());
        championship.setDescription(createChampionshipDto.getDescription());

        Optional<Championship> existingChampionship = findByName(createChampionshipDto.getName());
        if (existingChampionship.isPresent()) {
            throw new ChampionshipNameAlreadyExists("Championship with name '" + createChampionshipDto.getName() + "' already exists.");
        }

        Championship createdChampionship = championshipRepository.save(championship);

        return ChampionshipMapper.toDto(createdChampionship);
    }
}
