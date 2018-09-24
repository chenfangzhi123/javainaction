package org.yuanfang.rabbit.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
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
 * @Date: 2018/9/24-23:23
 * @ModifiedBy:
 */
@Slf4j
@ActiveProfiles(SpringConstant.CONSUMEER_CONFIRM_PROFILE)
public class ConsumerWithConfirmTest extends BaseTest {

    @Test
    @SneakyThrows
    public void process() {
        //为了防止交换器还没有创建
        Thread.sleep(1 * 1000);
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(
                this::send, 0, 3, TimeUnit.SECONDS);
        Thread.sleep(12 * 1000);
    }

    private void send() {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstant.CONFIRM_EXCHANGE, RabbitMQConstant.CONFIRM_KEY,
                new ExampleEvent(id.getAndIncrement(), "message from " + RabbitMQConstant.CONFIRM_KEY));
        } catch (Exception e) {
            log.error("send error !", e);
        }
    }
}