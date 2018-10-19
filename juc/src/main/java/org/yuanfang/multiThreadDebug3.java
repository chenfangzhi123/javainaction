package org.yuanfang;

import java.io.IOException;

/**
 * @Author: chenfangzhi
 * @Description: 研究java的thread运行状态和visualvm的对应关系
 * @Date: 2017/12/14-23:48
 * @ModifiedBy:
 */
public class multiThreadDebug3 {
    /**
     * 关系应该如下：
     * 1、VisualVM 运行(running) -- RUNNABLE；
     * 2、VisualVM 休眠（sleeping） -- TIMED_WAITING (sleeping)；
     * 3、VisualVM 等待（wait） -- WAITING (on object monitor)、 TIMED_WAITING (on object monitor)；
     * 4、VisualVM 驻留（park） -- WAITING (parking)、 TIMED_WAITING (parking)
     * 5、VisualVM 监视（monitor） -- BLOCKED (on object monitor)
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        SynBean synBean = new SynBean();
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                synchronized (synBean) {
                    while (true) {

                    }
                }
            }
        };
        t1.start();

        //模拟BLOCKED
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (synBean) {
                    System.out.println("进入方法！");
                }
            }
        };
        t2.start();

        //模拟wait状态
        Thread t3 = new Thread("t3") {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t3.start();

        //模拟sleep状态
        Thread t4 = new Thread("t4") {
            @Override
            public void run() {
                try {
                    sleep(12000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t4.start();

        //模拟TERMINATED  visualVM显示为白色
        Thread t5 = new Thread("t5") {
            @Override
            public void run() {
                try {
                    System.out.println("start");
                    Thread.sleep(10000);
                    System.out.println("end");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t5.start();

        //模拟NEW状态
        Thread t6 = new Thread("t6") {
            @Override
            public void run() {
                System.out.println("hello");
            }
        };

        //模拟io阻塞
        Thread t7 = new Thread("t7") {
            @Override
            public void run() {
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t7.start();


        // todoBychenfangzhi ----2017/12/15 0:15------>用LockSupport类模拟visualvm的驻留状态

        //模拟join状态
        t1.join();
    }


    public static class SynBean {
    }
}

