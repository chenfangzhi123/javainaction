package org.yuanfang.rabbit.config;

import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.yuanfang.BaseTest;
import org.yuanfang.rabbit.common.constant.SpringConstant;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/24-22:00
 * @ModifiedBy:
 */
@ActiveProfiles(value = {SpringConstant.CHANNEL_LISTENER_PROFILE, SpringConstant.OLD_CONFIG_PROFILE})
public class MyChannelListenerTest extends BaseTest {

    /**
     * 可以看到{@link MyChannelListener}的注册成功
     */
    @Test
    @SneakyThrows
    public void run() {
        System.in.read();
    }
}