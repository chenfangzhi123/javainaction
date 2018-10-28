package org.yuanfang.redis.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: chenfangzhi
 * @Description: 测试DefaultRedisMap的问题
 * @Date: 2018/10/28-19:58
 * @ModifiedBy:
 */
@Component
@Slf4j
public class MapTest {
    /**
     * 线程数量
     */
    public static final int THREAD_NUM = 5;
    /**
     * map中的元素个数，测试线程中的操作数暂时也用这个大小
     */
    private static final int THRESHOLD = 1000;
    /**
     * 测试的轮数
     */
    private static final int BARRIER_SIZE = 3;
    AtomicInteger count = new AtomicInteger(1);

    @Autowired
    StringRedisTemplate redisTemplate;
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(15);

    CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);

    CyclicBarrier cyclicBarrier = new CyclicBarrier(THREAD_NUM, new Runnable() {
        @Override
        public void run() {
            log.info("第{}轮结束！", count.getAndIncrement());
        }
    });

    /**
     * redis中保存的map
     */
    private DefaultRedisMap<String, String> map;

    @PostConstruct
    public void init() {
        map = new DefaultRedisMap("testMap", redisTemplate);

        //初始化数据
        for (int i = 0; i < THRESHOLD; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }

        for (int i = 0; i < THREAD_NUM; i++) {
            executorService.schedule(this::run, 5, TimeUnit.SECONDS);
        }
        executorService.execute(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AtomicInteger i = new AtomicInteger();
            map.forEach((s, s2) -> {
                if (!s.equals(s2)) {
                    i.getAndIncrement();
                }
            });
            System.out.println("错误的数据数量：" + i.get());
        });


    }

    public void run() {
        try {
            for (int i1 = 0; i1 < BARRIER_SIZE; i1++) {
                long l = System.currentTimeMillis();
                int i = ThreadLocalRandom.current().nextInt(0, THRESHOLD);
                for (int i2 = 0; i2 < THRESHOLD; i2++) {
                    map.computeIfPresent(String.valueOf(i), (s, s2) -> String.valueOf(Integer.valueOf(i) + 1));
                    map.computeIfPresent(String.valueOf(i), (s, s2) -> String.valueOf(Integer.valueOf(i) - 1));
                }
                System.out.println((System.currentTimeMillis() - l) / 1000);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();
    }

}
