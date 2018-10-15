package org.yuanfang;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/15-21:26
 * @ModifiedBy:
 */
public class ThreadExceptionTest {

    public static void main(String[] args) throws IOException {
        // Thread.setDefaultUncaughtExceptionHandler((t, e) -> System.out.println(t.getName()));
        // new Thread(() ->{
        //     throw new RuntimeException();
        // }).start();
        // Executors.newSingleThreadExecutor().execute(() -> {
        //     throw new RuntimeException();
        // });

        final Callable<Object> objectCallable = () -> {
            throw new RuntimeException();
        };
        //对于定时任务来说这个设置无效，因为里面跑的不直接是提交的任务，而是包装后的worker
        Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                final Thread thread = new Thread(r);
                thread.setUncaughtExceptionHandler((t, e) -> System.out.println(t.getName()));
                return thread;
            }
        }).schedule(objectCallable, 1, TimeUnit.SECONDS);
        System.in.read();
    }
}
