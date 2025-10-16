package com.example.colados_leaderboard_api.listener;

import com.example.colados_leaderboard_api.event.GameResultsCreatedFromProcessedMsg;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncEventListener {

    @Async("eventTaskExecutor")
    @EventListener
    public void handleAsyncEvent(GameResultsCreatedFromProcessedMsg event) {
        System.out.println("Asynchronously processing order: " + event.getGameId());
    }
}