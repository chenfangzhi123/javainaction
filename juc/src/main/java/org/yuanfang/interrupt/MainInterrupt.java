package org.yuanfang.interrupt;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/11/5-21:43
 * @ModifiedBy:
 */
public class MainInterrupt {
    public static void main(String[] args) throws InterruptedException {
        //创建一个任务
        Thread thread = new Thread(new TaskInterrupt());
        thread.start();
        Thread.sleep(3_000);
        thread.interrupt();
    }
}

@Slf4j
class TaskInterrupt implements Runnable {
    /**
     * 执行次数计数器
     */
    AtomicInteger adder = new AtomicInteger();

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            log.info("运行次数：{}", adder.incrementAndGet());
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                log.warn("随眠过程打断退出！");
                break;
            }
        }
        log.warn("退出运行！");
    }
}