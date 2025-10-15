package com.example.colados_leaderboard_api.consumer;

import com.example.colados_leaderboard_api.dto.ImageProcessedMsg;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @RabbitListener(queues = "${rabbitmq.queue-img-processed}")
    public void imgProcessedConsumer(ImageProcessedMsg message) {
        System.out.println("File Name: " + message.getFile_name());
        System.out.println("Status: " + message.getStatus());
        System.out.println("Results: " + message.getResults());
    }
}
