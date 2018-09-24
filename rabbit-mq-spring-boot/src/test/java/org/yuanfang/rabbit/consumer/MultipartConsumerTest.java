package org.yuanfang.rabbit.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.yuanfang.BaseTest;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.vo.ExampleEvent;
import org.yuanfang.rabbit.vo.ExampleEvent2;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/24-23:59
 * @ModifiedBy:
 */
@ActiveProfiles(SpringConstant.MULTIPART_PROFILE)
@Slf4j
public class MultipartConsumerTest extends BaseTest {

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
            rabbitTemplate.convertAndSend(RabbitMQConstant.MULTIPART_HANDLE_EXCHANGE, RabbitMQConstant.MULTIPART_HANDLE_KEY,
                new ExampleEvent(id.getAndIncrement(), "mulitpart message"));
            rabbitTemplate.convertAndSend(RabbitMQConstant.MULTIPART_HANDLE_EXCHANGE, RabbitMQConstant.MULTIPART_HANDLE_KEY,
                new ExampleEvent2(id.getAndIncrement(), "mulitpart message2"));
        } catch (Exception e) {
            log.error("send error !", e);
        }
    }
}