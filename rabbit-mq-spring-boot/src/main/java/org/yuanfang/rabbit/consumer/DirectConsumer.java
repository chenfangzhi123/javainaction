package org.yuanfang.rabbit.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/23-19:18
 * @ModifiedBy:
 */
@Component
@Slf4j
@Profile(SpringConstant.DIRECT_LISTENER_PROFILE)
public class DirectConsumer {

    @RabbitListener(
        bindings = @QueueBinding(
            exchange = @Exchange(value = RabbitMQConstant.DIRECT_EXCHANGE, type = ExchangeTypes.TOPIC,
                durable = RabbitMQConstant.FALSE_CONSTANT, autoDelete = RabbitMQConstant.TURE_CONSTANT),
            value = @Queue(value = RabbitMQConstant.DIRECT_QUEUE, durable = RabbitMQConstant.FALSE_CONSTANT,
                autoDelete = RabbitMQConstant.TURE_CONSTANT),
            key = RabbitMQConstant.DIRECT_KEY
        ),
        containerFactory = "directRabbitListenerContainerFactory"
    )
    public void process(ExampleEvent event) {
        log.info("direct container receive message:{} ", event);
    }
}
