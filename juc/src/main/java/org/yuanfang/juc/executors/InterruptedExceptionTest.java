package org.yuanfang.juc.executors;

import java.io.IOException;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/21-15:25
 * @ModifiedBy:
 */
public class InterruptedExceptionTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        // ExecutorService executorService = Executors.newFixedThreadPool(1);
        // AtomicReference<Thread> thread=new AtomicReference<>();
        // executorService.execute(() -> {
        //     thread.set(Thread.currentThread());
        //     Thread.currentThread().interrupt();
        //     System.out.println(Thread.currentThread().isInterrupted());
        //     System.out.println(Thread.currentThread());
        // });
        // Thread.sleep(1_000);
        // System.out.println(thread.get().isInterrupted());
        // executorService.execute(() ->  System.out.println(Thread.currentThread().isInterrupted()));
        Thread thread = new Thread(new D());
        thread.start();
        Thread.sleep(1_000);
        thread.interrupt();
    }

    public void test() {
        Thread.currentThread().interrupt();
        System.out.println(Thread.currentThread().isInterrupted());
        try {
            throw new InterruptedException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().isInterrupted());
    }

    public void test2(){

    }
}

class D implements Runnable {

    @Override
    public void run() {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().isInterrupted());
    }
}