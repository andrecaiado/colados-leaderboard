package com.example.colados_leaderboard_api.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange-img-submitted}")
    private String exchange;

    @Value("${rabbitmq.routing-key-img-submitted}")
    private String routingKey;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
