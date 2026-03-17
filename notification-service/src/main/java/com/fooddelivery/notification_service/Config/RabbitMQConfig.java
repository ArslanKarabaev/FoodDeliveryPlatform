package com.fooddelivery.notification_service.Config;

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
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable("order.created").build();
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return QueueBuilder.durable("order.confirmed").build();
    }

    @Bean
    public Queue orderCancelledQueue() {
        return QueueBuilder.durable("order.cancelled").build();
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
    public Queue deliveryCreatedQueue() {
        return QueueBuilder.durable("delivery.created").build();
    }

    @Bean
    public Queue deliveryStatusUpdatedQueue() {
        return QueueBuilder.durable("delivery.status.updated").build();
    }
}
