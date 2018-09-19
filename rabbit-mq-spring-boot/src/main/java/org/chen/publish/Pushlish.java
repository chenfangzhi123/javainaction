package org.chen.publish;

import lombok.extern.slf4j.Slf4j;
import org.chen.common.RabbitMQConstant;
import org.chen.config.ConfirmRabbitTemplate;
import org.chen.vo.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: chenfangzhi
 * @Description: 定时发送消息
 * @Date: 2018/9/19-22:38
 * @ModifiedBy:
 */
@Component
@Slf4j
public class Pushlish {
    AtomicInteger id = new AtomicInteger(1);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    @ConfirmRabbitTemplate
    RabbitTemplate rabbitTemplateWithTemplate;


    /**
     * 普通消息
     */
    @Scheduled(fixedDelay = 1 * 1000)
    public void sendTestMessage() {
        final int i = id.getAndIncrement();
        rabbitTemplate.convertAndSend(RabbitMQConstant.DEFAULT_EXCHANGE, RabbitMQConstant.DEFAULT_KEY,
            new Message(i, "message from " + RabbitMQConstant.DEFAULT_KEY));
    }


    /**
     * 发送需要confirm的消息
     */
    @Scheduled(fixedDelay = 5 * 1000)
    public void sendConfirmMessage() {
        final int i = id.getAndIncrement();
        rabbitTemplateWithTemplate.convertAndSend(RabbitMQConstant.DEFAULT_EXCHANGE, RabbitMQConstant.DEFAULT_KEY,
            new Message(i, "message from " + RabbitMQConstant.DEFAULT_KEY),
            new CorrelationData(Integer.toString(i)));
    }

    /**
     * 发送消息
     */
    @Scheduled(fixedDelay = 5 * 1000)
    public void sendOldMessage() {
        rabbitTemplate.convertAndSend(RabbitMQConstant.EXPLICIT_EXCHANGE, RabbitMQConstant.EXPLICIT_KEY,
            new Message((int) System.currentTimeMillis(), "message from " + RabbitMQConstant.EXPLICIT_KEY));
    }


}
