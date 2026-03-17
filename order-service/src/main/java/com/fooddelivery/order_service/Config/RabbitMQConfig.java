package com.fooddelivery.order_service.Config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return QueueBuilder.durable("payment.completed").build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable("payment.failed").build();
    }

    @Bean
    public Queue paymentRefundedQueue() {
        return QueueBuilder.durable("payment.refunded").build();
    }

    @Bean
    public Queue orderCancelledQueue() {
        return QueueBuilder.durable("order.cancelled").build();
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return QueueBuilder.durable("order.confirmed").build();
    }

    @Bean
    public Queue orderReadyQueue() {
        return QueueBuilder.durable("order.ready").build();
    }
}