package com.example.colados_leaderboard_api.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public final class BusinessProperties {
    @Value("${business-rules.game-max-score}")
    private int gameMaxScore;
}
