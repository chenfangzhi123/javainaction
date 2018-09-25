package org.yuanfang.rabbit.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.yuanfang.BaseTest;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.UUID;

import static org.yuanfang.rabbit.consumer.ReplyConsumer.REPLY;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/23-1:48
 * @ModifiedBy:
 */
@Slf4j
@ActiveProfiles(SpringConstant.REPLY_PROFILE)
public class ReplyConsumerTest extends BaseTest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 默认时长等待五秒否则返回null
     */
    @Test
    public void send() {
        final String id = UUID.randomUUID().toString();
        final String o = rabbitTemplate.convertSendAndReceiveAsType(
            RabbitMQConstant.RPC_EXCHANGE,
            RabbitMQConstant.RPC_KEY,
            new ExampleEvent((int) System.currentTimeMillis(), "Rpc message"),
            new CorrelationData(id),
            new ParameterizedTypeReference<String>() {
            });
        Assert.assertEquals(REPLY, o);
    }

    @Test
    @SneakyThrows
    public void call() {
        rabbitTemplate.convertAndSend(
            RabbitMQConstant.REPLY_EXCHANGE,
            RabbitMQConstant.REPLY_KEY,
            new ExampleEvent((int) System.currentTimeMillis(), "call message"));
        Thread.sleep(300 * 1000);
    }

}
