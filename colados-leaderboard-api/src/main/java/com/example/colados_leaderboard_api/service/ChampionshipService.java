package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.ChampionshipDto;
import com.example.colados_leaderboard_api.dto.CreateChampionshipDto;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.mapper.ChampionshipMapper;
import com.example.colados_leaderboard_api.repository.ChampionshipRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChampionshipService {

    private final ChampionshipRepository championshipRepository;

    public ChampionshipService(ChampionshipRepository championshipRepository) {
        this.championshipRepository = championshipRepository;
    }

    public Optional<Championship> getById(Integer id) {
        return championshipRepository.findById(id);
    }

    private Optional<Championship> getByName(String name) {
        return championshipRepository.findByName(name);
    }

    public ChampionshipDto createChampionship(CreateChampionshipDto createChampionshipDto) {
        validateChampionshipUniqueName(createChampionshipDto.getName(), null);

        Championship championship = new Championship();
        championship.setName(createChampionshipDto.getName());
        championship.setDescription(createChampionshipDto.getDescription());

        Championship createdChampionship = championshipRepository.save(championship);

        return ChampionshipMapper.toDto(createdChampionship);
    }

    public void updateChampionship(Integer id, CreateChampionshipDto updateChampionshipDto) throws EntityNotFound {
        Championship championshipToUpdate = championshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Championship not found with ID: " + id));

        validateChampionshipUniqueName(updateChampionshipDto.getName(), id);

        championshipToUpdate.setName(updateChampionshipDto.getName());
        championshipToUpdate.setDescription(updateChampionshipDto.getDescription());

        championshipRepository.save(championshipToUpdate);
    }

    private void validateChampionshipUniqueName(String name, Integer id) {
        Optional<Championship> existingChampionship = id == null ? getByName(name) : championshipRepository.findByNameAndIdNot(name, id);

        if (existingChampionship.isPresent()) {
            throw new DataIntegrityViolationException("Championship with name '" + name + "' already exists.");
        }
    }

    public void deleteChampionship(Integer id) throws EntityNotFound {
        Championship championshipToDelete = championshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Championship not found with ID: " + id));
        try {
            // Attempt to delete the championship
            championshipRepository.delete(championshipToDelete);
        } catch (DataIntegrityViolationException e) {
            // Handle the case where the championship is referenced by other entities
            throw new DataIntegrityViolationException("Cannot delete championship with ID: " + id + " as it is referenced by other entities.");
        }
    }
}
