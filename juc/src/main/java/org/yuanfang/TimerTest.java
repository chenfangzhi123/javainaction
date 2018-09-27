package org.yuanfang;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/27-14:08
 * @ModifiedBy:
 */
public class TimerTest {
    /**
     * Timer是单线程的，如果有多个任务，其中一个执行时间长会影响别的任务。
     * 如果一个任务抛出异常会直接向外抛出，推荐使用{@link ScheduledExecutorService}
     *
     * @param args
     */
    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("execute!");
                throw new RuntimeException();
            }
        }, 3 * 1000, 3 * 1000);

    }
}
