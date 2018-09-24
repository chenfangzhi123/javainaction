package org.yuanfang.rabbit.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.yuanfang.BaseTest;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;
import org.yuanfang.rabbit.config.annotation.ConfirmRabbitTemplate;
import org.yuanfang.rabbit.vo.ExampleEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/24-22:12
 * @ModifiedBy:
 */
@ActiveProfiles(SpringConstant.PUBLISHER_CONFIRM_PROFILE)
@Slf4j
public class PublishWithConfirmConfigTest extends BaseTest {

    @Autowired
    @ConfirmRabbitTemplate
    RabbitTemplate rabbitTemplateWithConfirm;

    /**
     * 发送需要confirm的消息
     */
    @Test
    @SneakyThrows
    public void sendConfirmMessage() {
        //为了防止交换器还没有创建
        Thread.sleep(1 * 1000);
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(
                this::send, 0, 3, TimeUnit.SECONDS);
        Thread.sleep(12 * 1000);
    }

    private void send() {
        try {
            final int i = id.getAndIncrement();
            rabbitTemplateWithConfirm.convertAndSend(RabbitMQConstant.DEFAULT_EXCHANGE, RabbitMQConstant.DEFAULT_KEY,
                new ExampleEvent(i, "confirm message id:" + i),
                new CorrelationData(Integer.toString(i)));
        } catch (Exception e) {
            log.error("send error !", e);
        }
    }

    /**
     * 发送没有队列的消息
     * ack仍然是true，如果设置了mandatory: true则会回调ReturnCallback
     */

    @Test
    @SneakyThrows
    public void sendMandatoryMessage() {
        final int i = id.getAndIncrement();
        rabbitTemplateWithConfirm.convertAndSend(RabbitMQConstant.DEFAULT_EXCHANGE, RabbitMQConstant.MANDATORY_KEY,
            new ExampleEvent((int) System.currentTimeMillis(), "no queue message id: " + i),
            new CorrelationData(Integer.toString(i)));
        Thread.sleep(2 * 1000);
    }


    /**
     * 发送没有交换器的消息,ack为false，不会抛异常,直接报错.
     * 可以配置application.yml,max-attempts:3会重新发送3次
     * 不会调用ReturnCallback方法
     */
    @Test
    @SneakyThrows
    public void sendNoExchangeMessage() {
        try {
            final int i = id.getAndIncrement();
            rabbitTemplate.convertAndSend("exchange.random." + i, RabbitMQConstant.MANDATORY_KEY,
                new ExampleEvent((int) System.currentTimeMillis(), "no exchange message id:" + i),
                new CorrelationData(Integer.toString(i)));
        } catch (Exception e) {
            log.error("no exchange message error", e);
        }
    }

}