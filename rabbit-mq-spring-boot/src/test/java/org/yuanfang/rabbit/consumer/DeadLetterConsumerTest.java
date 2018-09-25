package org.yuanfang.rabbit.consumer;

import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.yuanfang.BaseTest;
import org.yuanfang.rabbit.common.constant.SpringConstant;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/25-22:42
 * @ModifiedBy:
 */
@ActiveProfiles(SpringConstant.DEAD_LETTER_PROFILE)
public class DeadLetterConsumerTest extends BaseTest {

    @Test
    @SneakyThrows
    public void process() {
        System.in.read();
    }
}