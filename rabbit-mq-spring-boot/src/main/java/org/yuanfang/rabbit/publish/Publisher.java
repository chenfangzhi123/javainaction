package org.yuanfang.rabbit.publish;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/25-0:12
 * @ModifiedBy:
 */
@Component
public class Publisher {

    AtomicInteger id = new AtomicInteger();

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 普通消息
     */
    @Scheduled(fixedDelay = 5 * 1000)
    public void sendTestMessage() {
        rabbitTemplate.convertAndSend(RabbitMQConstant.DEFAULT_EXCHANGE, RabbitMQConstant.DEFAULT_KEY,
            new ExampleEvent(id.getAndIncrement(), "basic message !"));
    }
}
