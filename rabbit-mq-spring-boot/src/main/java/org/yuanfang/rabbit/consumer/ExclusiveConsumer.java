package org.yuanfang.rabbit.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.config.annotation.MyListener;
import org.yuanfang.rabbit.vo.ExampleEvent;

/**
 * @Author: chenfangzhi
 * @Description: 测试私有队列的功能, 可以在RabbitMq管理平台看到spring.gen开头的队列，
 * 绑定到了{@link RabbitMQConstant#DEFAULT_EXCHANGE}，详见{@link MyListener}
 * @Date: 2018/9/22-23:13
 * @ModifiedBy:
 */
@Slf4j
@Component
@Profile(SpringConstant.CONSUMER_EXCLUSIVE_PROFILE)
public class ExclusiveConsumer {

    @MyListener
    public void process(ExampleEvent message) {
        log.info("exclusive queue receive,id: {}, message:{}", message.getId(), message.getMessage());
    }

    @MyListener
    public void process2(ExampleEvent message) {
        log.info("exclusive queue2 receive,id: {}, message:{}", message.getId(), message.getMessage());
    }
}
