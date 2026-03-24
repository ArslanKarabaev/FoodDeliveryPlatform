package com.fooddelivery.notification_service.Config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
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
    public Queue paymentCompletedNotQueue() {
        return QueueBuilder.durable("payment.completed.not").build();
    }

    @Bean
    public Queue paymentFailedNotQueue() {
        return QueueBuilder.durable("payment.failed.not").build();
    }

    @Bean
    public Queue paymentRefundedNotQueue() {
        return QueueBuilder.durable("payment.refunded.not").build();
    }

    @Bean
    public Queue deliveryCreatedQueue() {
        return QueueBuilder.durable("delivery.created").build();
    }

    @Bean
    public Queue deliveryStatusUpdatedQueue() {
        return QueueBuilder.durable("delivery.status.updated").build();
    }

    @Bean
    public Queue passwordResetQueue() { return QueueBuilder.durable("password.reset").build(); }
}
