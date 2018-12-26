package org.yuanfang.interrupt;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/11/5-21:43
 * @ModifiedBy:
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        //创建一个任务
        Task task = new Task();
        new Thread(task).start();
        Thread.sleep(3_000);
        task.stop = true;
    }
}

@Slf4j
class Task implements Runnable {
    /**
     * 是否停止的标志位
     */
    public volatile boolean stop = false;
    /**
     * 执行次数计数器
     */
    AtomicInteger adder = new AtomicInteger();

    @Override
    public void run() {
        while (!stop) {
            log.info("运行次数：{}", adder.incrementAndGet());
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.warn("退出运行！");
    }
}