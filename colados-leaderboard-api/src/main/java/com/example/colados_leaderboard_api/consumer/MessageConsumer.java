package com.example.colados_leaderboard_api.consumer;

import com.example.colados_leaderboard_api.dto.ImageProcessedMsg;
import com.example.colados_leaderboard_api.service.GameService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private final GameService gameService;

    public MessageConsumer(GameService gameService) {
        this.gameService = gameService;
    }

    @RabbitListener(queues = "${rabbitmq.queue-img-processed}")
    public void imgProcessedConsumer(ImageProcessedMsg message) throws Exception {
        this.gameService.updateGameFromProcessedMsg(message);
    }
}
