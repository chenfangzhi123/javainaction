package org.yuanfang.rabbit.consumer;

import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.yuanfang.BaseTest;
import org.yuanfang.rabbit.common.constant.SpringConstant;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/25-23:06
 * @ModifiedBy:
 */
@ActiveProfiles(SpringConstant.CONSUMER_RETRY_PROFILE)
public class ConsumerWithRetryTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        System.in.read();
    }
}