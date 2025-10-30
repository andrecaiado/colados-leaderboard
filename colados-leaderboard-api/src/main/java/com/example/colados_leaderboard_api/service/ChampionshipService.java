package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.ChampionshipDto;
import com.example.colados_leaderboard_api.dto.CreateChampionshipDto;
import com.example.colados_leaderboard_api.entity.Championship;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.mapper.ChampionshipMapper;
import com.example.colados_leaderboard_api.repository.ChampionshipRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ChampionshipService {

    private final ChampionshipRepository championshipRepository;

    public ChampionshipService(ChampionshipRepository championshipRepository) {
        this.championshipRepository = championshipRepository;
    }

    public Championship getById(Integer id) throws EntityNotFound {
        return championshipRepository.findById(id).orElseThrow(() -> new EntityNotFound("Championship not found with ID: " + id));
    }

    public ChampionshipDto createChampionship(CreateChampionshipDto createChampionshipDto) {
        ensureChampionshipNameIsUnique(createChampionshipDto.getName(), null);

        Championship championship = new Championship();
        championship.setName(createChampionshipDto.getName());
        championship.setDescription(createChampionshipDto.getDescription());

        Championship createdChampionship = championshipRepository.save(championship);

        return ChampionshipMapper.toDto(createdChampionship);
    }

    public void updateChampionship(Integer id, CreateChampionshipDto updateChampionshipDto) throws EntityNotFound {
        Championship championshipToUpdate = getById(id);

        ensureChampionshipNameIsUnique(updateChampionshipDto.getName(), id);

        championshipToUpdate.setName(updateChampionshipDto.getName());
        championshipToUpdate.setDescription(updateChampionshipDto.getDescription());

        championshipRepository.save(championshipToUpdate);
    }

    private void ensureChampionshipNameIsUnique(String name, Integer currentChampionshipId) {
        Optional<Championship> existing = championshipRepository.findByName(name);
        if (existing.isPresent() && !Objects.equals(existing.get().getId(), currentChampionshipId)) {
            throw new DataIntegrityViolationException("Championship with name '" + name + "' already exists.");
        }
    }

    public void deleteChampionship(Integer id) throws EntityNotFound {
        Championship championshipToDelete = getById(id);
        try {
            // Attempt to delete the championship
            championshipRepository.delete(championshipToDelete);
        } catch (DataIntegrityViolationException e) {
            // Handle the case where the championship is referenced by other entities
            throw new DataIntegrityViolationException("Cannot delete championship with ID: " + id + " as it is referenced by other entities.");
        }
    }

    public ChampionshipDto getChampionshipById(Integer id) throws EntityNotFound {
        return ChampionshipMapper.toDto(getById(id));
    }

    public Iterable<ChampionshipDto> getAllChampionships() {
        Iterable<Championship> championships = championshipRepository.findAll(Sort.by("name"));
        return ChampionshipMapper.toDtoList(championships);
    }
}
