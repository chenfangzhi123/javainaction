package org.yuanfang.juc.executors;

import java.util.concurrent.SynchronousQueue;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/11/5-23:17
 * @ModifiedBy:
 */
public class SynchronousQueueTest {
    public static void main(String[] args) {
        SynchronousQueue<Integer> syn = new SynchronousQueue<>();
        System.out.println(syn.offer(123));
        syn.offer(123);
        syn.offer(123);
        syn.offer(123);



    }
}
