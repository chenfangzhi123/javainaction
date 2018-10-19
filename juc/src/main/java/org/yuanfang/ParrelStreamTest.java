package org.yuanfang;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/20-0:28
 * @ModifiedBy:
 */
public class ParrelStreamTest {
    static ExecutorService executorService = Executors.newFixedThreadPool(4);
    static ExecutorService forkPool = Executors.newWorkStealingPool(4);

    public static void main(String[] args) throws InterruptedException {
        executorService.execute(ParrelStreamTest::run);
        Thread.sleep(2_000);

        forkPool.execute(ParrelStreamTest::run);
        Thread.sleep(2_000);

        /**
         *
         */
        run();

    }

    private static void run() {
        Arrays.asList(1, 32, 42, 6).parallelStream().map(integer -> {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread()+""+Thread.currentThread().isDaemon());
            return null;
        }).toArray();
    }
}
