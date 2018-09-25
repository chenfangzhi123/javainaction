package org.yuanfang.rabbit.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.Map;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/25-21:51
 * @ModifiedBy:
 */
@Component
@Slf4j
@Profile(SpringConstant.CONSUMER_REQUEUE_PROFILE)
public class RequeueConsumer {

    /**
     * 默认情况下{@link SimpleRabbitListenerContainerFactory}没有配置{@link RetryTemplate}时，抛出异常时，spring会发送nack，
     * 然后消息会重新入队列，这样会导致消息不停的被消费重新入队列，陷入死循环。可以通过抛出{@link AmqpRejectAndDontRequeueException}
     * 告诉监听容器不要发送nack时不要重新入队列。
     * 或者配置{@link RetryTemplate}进行应用内重试。
     *
     * @param headers
     * @param msg
     */
    @RabbitListener(
        bindings = @QueueBinding(
            exchange = @Exchange(value = RabbitMQConstant.DEFAULT_EXCHANGE, type = ExchangeTypes.TOPIC,
                durable = RabbitMQConstant.FALSE_CONSTANT, autoDelete = RabbitMQConstant.TURE_CONSTANT),
            value = @Queue(value = RabbitMQConstant.DEFAULT_QUEUE, durable = RabbitMQConstant.FALSE_CONSTANT,
                autoDelete = RabbitMQConstant.TURE_CONSTANT),
            key = RabbitMQConstant.DEFAULT_KEY
        ),
        containerFactory = "container"
    )
    public void process(@Headers Map<String, Object> headers, @Payload ExampleEvent msg) {
        log.info("error consumer receive message:{headers = [" + headers + "], msg = [" + msg + "]}");
        throw new RuntimeException("receive error!");
    }
}
