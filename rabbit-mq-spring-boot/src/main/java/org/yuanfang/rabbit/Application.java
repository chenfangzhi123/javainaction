package org.yuanfang.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;
import org.yuanfang.rabbit.common.constant.SpringConstant;

import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class Application {

    /**
     * 默认只会向{@link RabbitMQConstant#DEFAULT_EXCHANGE}发送消息，需要使用测试类打开{@linkplain SpringConstant Profile}
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 定时任务需要的线程池，默认会创建一个单线程的线程池，这里我只是不喜欢默认的线程名字所以重写了
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(2, new ThreadFactory() {

            public final AtomicInteger id = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, MessageFormat.format("spring-schedule-{0}", id.getAndIncrement()));
            }
        });
    }
}
