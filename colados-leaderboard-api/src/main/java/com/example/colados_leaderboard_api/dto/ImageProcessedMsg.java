package com.example.colados_leaderboard_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class ImageProcessedMsg {
    private String file_name;
    private List<Map<String, Object>> results;
    private String status;
}
