package org.chen.consume;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/18-21:07
 * @ModifiedBy:
 */

import lombok.extern.slf4j.Slf4j;
import org.chen.common.RabbitMQConstant;
import org.chen.vo.Message;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 注意这里是三个Listener，会创建三个监听的容器，每个容器可以存放多个消费者通过concurrency参数控制，
 * 详情可见yml配置文件的注释。消息不会被重复消费，只会有一个RabbitListener处理
 */
@Component
@Slf4j
public class Consumer1 {
    /**
     * 利用了通配符
     */
    private static final String DKEY = "default.#";

    /**
     * 可以直接通过注解声明交换器、绑定、队列。但是如果声明的和rabbitMq中已经存在的不一致的话
     * 会报错，一般队列这种都是一次性以后一直存在了，可以用后面两种声明
     *
     * @param headers
     * @param msg
     */
    @RabbitListener(
        bindings = @QueueBinding(
            exchange = @Exchange(value = RabbitMQConstant.DEFAULT_EXCHANGE, type = ExchangeTypes.TOPIC),
            value = @Queue(value = RabbitMQConstant.DEFAULT_QUEUE, durable = RabbitMQConstant.TURE_CONSTANT),
            key = DKEY
        )
    )
    public void process(@Headers Map<String, Object> headers, @Payload Message msg) {
        log.info("Consume1 方法1 param:{headers = [" + headers + "], msg = [" + msg + "]} info:");
    }

    @RabbitListener(queuesToDeclare = {@Queue(name = RabbitMQConstant.DEFAULT_QUEUE)})
    public void process2(@Payload Message message, @Header(name = "amqp_deliveryTag") String deliveryTag) {
        log.info("Consume1 方法2 param:{message = [" + message + "], deliveryTag = [" + deliveryTag + "]} info:");
    }

    // todoBychenfangzhi ----2018/9/20 0:55------>

    /**
     * 这个注解是可以重复的
     *
     * @param message
     * @return
     */
    @RabbitListener(queues = RabbitMQConstant.DEFAULT_QUEUE)
    @RabbitListener(queues = RabbitMQConstant.EXPLICIT_QUEUE)
    @SendTo(RabbitMQConstant.EXPLICIT_QUEUE)
    public Message process3(Message message) {
        log.info("Consume1 方法3 param:{message = [" + message + "]} info:");
        return new Message(123, "hou");
    }

}
