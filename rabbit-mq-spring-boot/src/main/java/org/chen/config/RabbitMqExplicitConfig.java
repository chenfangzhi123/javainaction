package org.chen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author: chenfangzhi
 * @Description: 普通的spring配置见 {@link SpringOldConfig}
 * @Date: 2018/9/19-21:45
 * @ModifiedBy:
 */
@Configuration
@Slf4j
public class RabbitMqExplicitConfig {

    /**
     * 默认采用了java的序列化，性能比较低，而且阅读不友好
     *
     * @return
     */
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 重要的消息需要回调则采用@ConfirmRabbitTemplate注解进行限定，不重要的优先使用普通的。
     * 如果不分开实例化template的话，在发送消息是传递CorrelationData的也会回调。
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    @ConfirmRabbitTemplate
    RabbitTemplate rabbitTemplateWithConfirm(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("确认回调 id:{},ack:{},cause:{}", correlationData, ack, cause));
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }


    @Bean
    @Primary
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }


}
