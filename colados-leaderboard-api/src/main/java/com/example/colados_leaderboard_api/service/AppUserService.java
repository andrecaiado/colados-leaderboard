package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.AppUserDto;
import com.example.colados_leaderboard_api.mapper.AppUserMapper;
import com.example.colados_leaderboard_api.repository.AppUserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Iterable<AppUserDto> getAll() {
        return AppUserMapper.toDtoList(appUserRepository.findAll(Sort.by("username")));
    }
}
