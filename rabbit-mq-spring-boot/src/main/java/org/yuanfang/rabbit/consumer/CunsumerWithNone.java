package org.yuanfang.rabbit.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.Map;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/25-21:05
 * @ModifiedBy:
 */
@Component
@Slf4j
@Profile(SpringConstant.CONSUMER_NONE_PROFILE)
public class CunsumerWithNone {

    @RabbitListener(
        bindings = @QueueBinding(
            exchange = @Exchange(value = RabbitMQConstant.NONE_EXCHANGE, type = ExchangeTypes.TOPIC),
            value = @Queue(value = RabbitMQConstant.NONE_QUEUE, durable = RabbitMQConstant.TURE_CONSTANT),
            key = RabbitMQConstant.NONE_KEY
        ),
        containerFactory = "containerWithNone"
    )
    @SneakyThrows
    public void process(@Headers Map<String, Object> headers, @Payload ExampleEvent msg) {
        Thread.sleep(1 * 1000);
        log.info("none consumer receive message:{headers = [" + headers + "], msg = [" + msg + "]}");
    }
}
