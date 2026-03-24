package com.fooddelivery.payment_service.Config;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
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
    public Queue paymentCompletedQueue(){ return QueueBuilder.durable("payment.completed").build();}

    @Bean
    public Queue paymentCompletedNotQueue(){ return QueueBuilder.durable("payment.completed.not").build();}

    @Bean
    public Queue paymentFailedQueue(){ return QueueBuilder.durable("payment.failed").build();}

    @Bean
    public Queue paymentFailedNotQueue(){ return QueueBuilder.durable("payment.failed.not").build();}

    @Bean
    public Queue paymentRefundedQueue(){ return QueueBuilder.durable("payment.refunded").build();}

    @Bean
    public Queue paymentRefundedNotQueue(){ return QueueBuilder.durable("payment.refunded.not").build();}

}
