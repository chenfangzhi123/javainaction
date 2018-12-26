package org.yuanfang;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/12/26-11:23
 * @ModifiedBy:
 */
public class InterruptTest {
    public static void main(String[] args) throws IOException {
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println(Thread.currentThread());
            Thread.interrupted();
        }, 1, 1, TimeUnit.SECONDS);
        System.in.read();
    }
}
