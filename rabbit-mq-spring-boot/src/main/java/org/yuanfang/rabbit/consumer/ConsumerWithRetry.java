package org.yuanfang.rabbit.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.Map;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/25-22:12
 * @ModifiedBy:
 */
@Component
@Slf4j
@Profile(SpringConstant.CONSUMER_RETRY_PROFILE)
public class ConsumerWithRetry {

    /**
     * 注意下面{@link MessageListenerContainer}采用了默认的{@link EnableRabbit}自动注册的监听器。
     * 在application.yml中配置了{@link RetryTemplate},并且重试次数为3。{@link RetryTemplate}默认才重试次数到达后，
     * 默认的{@link RejectAndDontRequeueRecoverer}仅仅是抛出异常，打印错误，发送nack,消息不会重新入队列，
     * 你唯一可以找到消息的地方就是日志了。另外注意这3次重试是在应用内的行为，RabbitMq是未感知的。
     * 一般可以采用如下的方案：
     * <li>使用{@link RepublishMessageRecoverer}这个{@link MessageRecoverer}会发送发送消息到指定队列</li>
     * <li>给队列绑定死信队列，这样抛出一场是这种方式和上面的结果一样都是转发到了另外一个队列。详见{@link DeadLetterConsumer}</li>
     * <li>注册自己实现的{@link MessageRecoverer}</li>
     * <li>给{@link MessageListenerContainer}设置{@link RecoveryCallback}</li>
     * <li>对于方法手动捕获异常，进行处理</li>
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
        )
    )
    public void process(@Headers Map<String, Object> headers, @Payload ExampleEvent msg) {
        log.info("retry consumer receive message:{headers = [" + headers + "], msg = [" + msg + "]}");
        throw new RuntimeException("receive error!");
    }
}
