package org.yuanfang.rabbit.consumer;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/18-21:07
 * @ModifiedBy:
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;
import org.yuanfang.rabbit.vo.ExampleEvent2;
import org.yuanfang.rabbit.vo.ExampleEvent3;

@Component
@Slf4j
@RabbitListener(
    bindings = @QueueBinding(
        exchange = @Exchange(value = RabbitMQConstant.MULTIPART_HANDLE_EXCHANGE, type = ExchangeTypes.TOPIC,
            durable = RabbitMQConstant.FALSE_CONSTANT, autoDelete = RabbitMQConstant.TURE_CONSTANT),
        value = @Queue(value = RabbitMQConstant.MULTIPART_HANDLE_QUEUE, durable = RabbitMQConstant.FALSE_CONSTANT,
            autoDelete = RabbitMQConstant.TURE_CONSTANT),
        key = RabbitMQConstant.MULTIPART_HANDLE_KEY
    )
)
@Profile(SpringConstant.MULTIPART_PROFILE)
public class MultipartConsumer {

    /**
     * RabbitHandler用于有多个方法时但是参数类型不能一样，否则会报错
     *
     * @param msg
     */
    @RabbitHandler
    public void process(ExampleEvent msg) {
        log.info("param:{msg = [" + msg + "]} info:");
    }

    @RabbitHandler
    public void processMessage2(ExampleEvent2 msg) {
        log.info("param:{msg2 = [" + msg + "]} info:");
    }

    /**
     * 下面的多个消费者，消费的类型不一样没事，不会被调用，但是如果缺了相应消息的处理Handler则会报错
     *
     * @param msg
     */
    @RabbitHandler
    public void processMessage3(ExampleEvent3 msg) {
        log.info("param:{msg3 = [" + msg + "]} info:");
    }


}
