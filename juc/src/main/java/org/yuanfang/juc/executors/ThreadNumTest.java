package org.yuanfang.juc.executors;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/11/3-0:06
 * @ModifiedBy:
 */
@Slf4j
public class ThreadNumTest {
    // public static void main(String[] args) {
    //     ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 100000, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    //     for (int i = 0; i < 1000; i++) {
    //         threadPoolExecutor.execute(() -> {
    //             try {
    //                 System.in.read();
    //             } catch (IOException e) {
    //                 e.printStackTrace();
    //             }
    //         });
    //     }
    //
    // }

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));
        for (int i = 0; i < 10; i++) {
            try {
                log.info("投入任务：{}", i);
                threadPoolExecutor.execute(() -> {
                    try {
                        Thread.sleep(100_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                Thread.sleep(1000);

            } catch (Exception e) {
                log.error("投入任务失败：{}", i, e);
            }

        }
    }
}
