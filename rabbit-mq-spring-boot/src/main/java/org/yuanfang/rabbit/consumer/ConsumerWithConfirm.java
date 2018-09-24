package org.yuanfang.rabbit.consumer;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.Map;

/**
 * @Author: chenfangzhi
 * @Description: 带确认模式的消费者
 * @Date: 2018/9/20-1:26
 * @ModifiedBy:
 */
@Slf4j
@Component
@Profile(SpringConstant.CONSUMEER_CONFIRM_PROFILE)
public class ConsumerWithConfirm {

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
        exchange = @Exchange(value = RabbitMQConstant.CONFIRM_EXCHANGE, type = ExchangeTypes.TOPIC,
            durable = RabbitMQConstant.FALSE_CONSTANT, autoDelete = RabbitMQConstant.TURE_CONSTANT),
        value = @Queue(value = RabbitMQConstant.CONFIRM_QUEUE, durable = RabbitMQConstant.FALSE_CONSTANT,
            autoDelete = RabbitMQConstant.TURE_CONSTANT),
        key = RabbitMQConstant.CONFIRM_KEY),
        containerFactory = "containerWithConfirm")
    public void process(ExampleEvent msg, Channel channel, @Header(name = "amqp_deliveryTag") long deliveryTag,
                        @Header("amqp_redelivered") boolean redelivered, @Headers Map<String, String> head) {
        try {
            log.info("ConsumerWithConfirm receive message:{},header:{}", msg, head);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("consume confirm error!", e);
            //这一步千万不要忘记，不会会导致消息未确认，消息到达连接的qos之后便不能再接收新消息
            //一般重试肯定的有次数，这里简单的根据是否已经重发过来来决定重发。第二个参数表示是否重新分发
            channel.basicReject(deliveryTag, !redelivered);
        }
    }
}

