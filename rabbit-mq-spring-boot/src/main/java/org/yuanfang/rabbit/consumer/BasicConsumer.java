package org.yuanfang.rabbit.consumer;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/18-21:07
 * @ModifiedBy:
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.Map;

/**
 * 注意这里是三个Listener，会创建三个监听的容器，每个容器可以存放多个消费者通过concurrency参数控制，
 * 详情可见yml配置文件的注释。消息不会被重复消费，只会有一个RabbitListener处理
 */
@Component
@Slf4j
public class BasicConsumer {

    /**
     * 利用了通配符
     */
    private static final String DKEY = "key.default.#";

    /**
     * 可以直接通过注解声明交换器、绑定、队列。但是如果声明的和rabbitMq中已经存在的不一致的话
     * 会报错，一般队列这种都是一次性以后一直存在了，可以用后面两种声明,
     * 便于测试，我这里都是不使用持久化，没有消费者之后自动删除
     * {@link RabbitListener}是可以重复的。并且声明队列绑定的key也可以有多个.
     *
     * <pre class="code">
     *     &#064;RabbitListener(queues = RabbitMQConstant.DEFAULT_QUEUE,containerFactory = "container")
     *     &#064;RabbitListener(queues = RabbitMQConstant.MULTIPART_QUEUE,containerFactory = "container")
     *     public void mutilepart(ExampleEvent message, &#064;Header(name = "amqp_deliveryTag") String deliveryTag) {
     *         log.info("Consume1 mutilepart param:{message = [" + message + "]} info:{}", deliveryTag);
     *     }
     * </pre>
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
            key = DKEY
        ),
        containerFactory = "container",
        //指定消费者的线程数量,一个线程会打开一个Channel，一个队列上的消息只会被消费一次（不考虑消息重新入队列的情况）
        concurrency = "5-10"
    )
    public void process(@Headers Map<String, Object> headers, @Payload ExampleEvent msg) {
        log.info("basic consumer receive message:{headers = [" + headers + "], msg = [" + msg + "]}");
    }

    // /**
    //  * {@link Queue#ignoreDeclarationExceptions}声明队列会忽略错误，这个消费者仍然是可用的
    //  *
    //  * @param headers
    //  * @param msg
    //  */
    // @RabbitListener(queuesToDeclare = @Queue(value = RabbitMQConstant.DEFAULT_QUEUE, ignoreDeclarationExceptions = RabbitMQConstant.TURE_CONSTANT))
    // public void process2(@Headers Map<String, Object> headers, @Payload ExampleEvent msg) {
    //     log.info("basic2 consumer receive message:{headers = [" + headers + "], msg = [" + msg + "]}");
    // }
}
