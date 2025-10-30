package com.example.colados_leaderboard_api.consumer;

import com.example.colados_leaderboard_api.dto.ImageProcessedMsg;
import com.example.colados_leaderboard_api.service.GameService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private final GameService gameService;

    public MessageConsumer(GameService gameService) {
        this.gameService = gameService;
    }

    @RabbitListener(queues = "${rabbitmq.queue-img-processed}", ackMode = "MANUAL")
    public void imgProcessedConsumer(ImageProcessedMsg message, Channel channel, Message amqpMessage) throws Exception {
        try {
            this.gameService.updateGameFromProcessedMsg(message);
        } finally {
            channel.basicAck(amqpMessage.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
