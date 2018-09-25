package org.yuanfang.rabbit.consumer;

import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.test.context.ActiveProfiles;
import org.yuanfang.BaseTest;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/25-21:09
 * @ModifiedBy:
 */
@ActiveProfiles(SpringConstant.CONSUMER_NONE_PROFILE)
public class CunsumerWithNoneTest extends BaseTest {

    /**
     * 当将消费者确认模式设置为{@linkplain AcknowledgeMode#NONE 自动确认}时，rabbitmq会将所有的消息都推送给消费者，可以在
     * rabbitmq的web控制台查看队列的消息数目。
     * 这个属性可能会导致消息堆积，而且如果消费者挂了，缓存的消息也就丢失了。
     * 默认的{@linkplain AcknowledgeMode#AUTO 自动}不是rabbitmq的，其实是将消费者设置成了手动确认，然后spring的监听容器
     * 根据你的消息是否抛出异常返回ack和nack。
     * 一般不要使用rabbitmq的自动确认，除非是一些非常不重要的数据，比如性能统计，为了追求高的吞吐量。
     */
    @Test
    @SneakyThrows
    public void process() {
        //为了防止交换器还没有创建
        Thread.sleep(1 * 1000);
        for (int i = 0; i < 1000; i++) {
            rabbitTemplate.convertAndSend(RabbitMQConstant.NONE_EXCHANGE, RabbitMQConstant.NONE_KEY,
                new ExampleEvent(id.getAndIncrement(), "auto message "));
        }
        Thread.sleep(100 * 1000);
    }
}