package org.yuanfang.juc.executors;

import lombok.SneakyThrows;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/27-18:22
 * @ModifiedBy:
 */
public class ScheduleSingleThreadTest {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        scheduledExecutorService.scheduleAtFixedRate(ScheduleSingleThreadTest::task, 1, 1, TimeUnit.SECONDS);

    }

    @SneakyThrows
    public static void task() {
        System.out.println("run "+System.currentTimeMillis()/1000);
        Thread.sleep(3 * 1000);
    }
}
