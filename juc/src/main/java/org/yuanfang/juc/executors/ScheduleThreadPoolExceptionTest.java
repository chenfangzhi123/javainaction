package org.yuanfang.juc.executors;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/21-23:20
 * @ModifiedBy:
 */
public class ScheduleThreadPoolExceptionTest {
    public static void main(String[] args) throws IOException {
        // 定时任务线程不抛出异常
        // ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1) {
        //     @Override
        //     protected void beforeExecute(Thread t, Runnable r) {
        //         super.beforeExecute(t, r);
        //     }
        //
        //     @Override
        //     protected void afterExecute(Runnable r, Throwable t) {
        //         if (t != null) {
        //             System.out.println(t.toString());
        //         }
        //     }
        // };

        // scheduledThreadPoolExecutor.schedule(() -> {
        //     System.out.println("hello");
        //     throw new RuntimeException();
        // }, 2, TimeUnit.SECONDS);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t != null) {
                    System.out.println(t.toString());
                }
            }
        };
        threadPoolExecutor.execute(() -> {
            throw new RuntimeException();
        });

        //submit是不会将异常传递到afterExecute方法的参数的
        threadPoolExecutor.submit(() -> {
            throw new RuntimeException();
        });

        System.in.read();
    }
}
