package com.example.colados_leaderboard_api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameResultsCreatedFromProcessedMsg extends ApplicationEvent {
    private final Integer gameId;

    public GameResultsCreatedFromProcessedMsg(Object source, Integer gameId) {
        super(source);
        this.gameId = gameId;
    }

}
