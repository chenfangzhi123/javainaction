package org.yuanfang.rabbit.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.yuanfang.rabbit.common.constant.SpringConstant;

/**
 * @Author: chenfangzhi
 * @Description: channel监听器，这里只是简单打印
 * @Date: 2018/9/22-16:42
 * @ModifiedBy:
 */
@Component
@Profile(SpringConstant.CHANNEL_LISTENER_PROFILE)
@Slf4j
public class MyChannelListener implements ChannelListener {

    @Override
    public void onCreate(Channel channel, boolean transactional) {
        log.info("create channel:{channel = [" + channel + "], transactional = [" + transactional + "]} ");
    }

    @Override
    public void onShutDown(ShutdownSignalException signal) {
        log.info("channel is close,reason:{}", signal.getReason());
    }
}
