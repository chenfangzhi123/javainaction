package org.yuanfang.rabbit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.config.annotation.ConfirmRabbitTemplate;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/24-22:08
 * @ModifiedBy:
 */
@Component
@Slf4j
@Profile(SpringConstant.PUBLISHER_CONFIRM_PROFILE)
public class PublishWithConfirmConfig {

    /**
     * 重要的消息需要回调则采用@ConfirmRabbitTemplate注解进行限定，不重要的优先使用普通的。
     * 如果不分开实例化template的话，在发送消息是传递CorrelationData的也会回调。
     *
     * @param connectionFactory
     *
     * @return
     */
    @Bean
    @ConfirmRabbitTemplate
    RabbitTemplate rabbitTemplateWithConfirm(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 每次发送消息都会回调这个方法
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause)
            -> log.info("confirm callback id:{},ack:{},cause:{}", correlationData, ack, cause));
        //注意这里需要手动指定，指定之后，如果消息发送到一个交换器，但是匹配不到一个队列，对调用Return回调
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey)
            -> log.info("return callback message：{},code:{},text:{}", message, replyCode, replyText));
        rabbitTemplate.setMessageConverter(messageConverter);
        /**
         * 默认是org.springframework.amqp.rabbit.core.RabbitTemplate.messageTagProvider自增作为id，
         * 填充的地方在{@link RabbitTemplate#doSendAndReceiveAsListener }调用{@link RabbitTemplate#saveAndSetProperties}
         * {@link RabbitTemplate#saveAndSetProperties}。
         * 如果下面的设置为true，自己在发送消息前设置消息头correlationId才会生效。一般没有设置的必要.
         * 注意这个和发布者确认模式的{@link org.springframework.amqp.rabbit.support.CorrelationData}产生的id没有关系
         * rabbitTemplate.setUserCorrelationId(true);
         */

        return rabbitTemplate;
    }
}
