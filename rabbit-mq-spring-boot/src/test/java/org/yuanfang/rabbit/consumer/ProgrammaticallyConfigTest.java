package org.yuanfang.rabbit.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.yuanfang.BaseTest;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/24-22:23
 * @ModifiedBy:
 */
@ActiveProfiles(SpringConstant.PROGRAMMATICALLY_CONFIG_PROFILE)
@Slf4j
public class ProgrammaticallyConfigTest extends BaseTest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    @SneakyThrows
    public void pushsh() {
        //为了防止交换器还没有创建
        Thread.sleep(1 * 1000);
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(
                this::send, 0, 3, TimeUnit.SECONDS);
        Thread.sleep(12 * 1000);
    }

    private void send() {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstant.PROGRAMMATICALLY_EXCHANGE, RabbitMQConstant.PROGRAMMATICALLY_KEY,
                new ExampleEvent(id.getAndIncrement(), "message from " + RabbitMQConstant.PROGRAMMATICALLY_KEY));
        } catch (Exception e) {
            log.error("send error !", e);
        }
    }
}