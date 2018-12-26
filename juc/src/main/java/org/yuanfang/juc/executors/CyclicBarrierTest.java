package org.yuanfang.juc.executors;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/22-21:55
 * @ModifiedBy:
 */
public class CyclicBarrierTest {
    public static void main(String[] args) throws IOException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(4, () -> {
            System.out.println(Thread.currentThread());
        });
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        System.in.read();
    }
}
