package com.example.colados_leaderboard_api.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "app.constants")
public class AppConstants {
    @Value("${app.constants.game-max-score}")
    private int gameMaxScore;
}
