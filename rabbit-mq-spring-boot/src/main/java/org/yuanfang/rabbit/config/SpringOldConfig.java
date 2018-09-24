package org.yuanfang.rabbit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.SendRetryContextAccessor;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.yuanfang.rabbit.common.constant.SpringConstant;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: chenfangzhi
 * @Description: 这里的配置就是普通spring的配置方法，在配置类加上@EnableRabbit,
 * springBoot比spring只是帮你自动配置了ConnectionFactory，MessageListenerContainer并且开启了EnableRabbit
 * @Date: 2018/9/19-21:53
 * @ModifiedBy:
 */
@Slf4j
@Profile(SpringConstant.OLD_CONFIG_PROFILE)
@Configuration
@EnableRabbit
public class SpringOldConfig {

    @Autowired(required = false)
    List<ChannelListener> listeners;

    /**
     * 支持@Value用法
     *
     * @return
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("application.yml"));
        return propertySourcesPlaceholderConfigurer;
    }

    /**
     * 配置连接工厂
     *
     * @param port
     * @param host
     * @param userName
     * @param password
     * @param isConfirm
     * @param vhost
     *
     * @return
     */
    @Bean
    ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.port}") int port,
                                        @Value("${spring.rabbitmq.host}") String host,
                                        @Value("${spring.rabbitmq.username}") String userName,
                                        @Value("${spring.rabbitmq.password}") String password,
                                        @Value("${spring.rabbitmq.publisher-confirms}") boolean isConfirm,
                                        @Value("${spring.rabbitmq.virtual-host}") String vhost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setPublisherConfirms(isConfirm);
        if (!CollectionUtils.isEmpty(listeners)) {
            connectionFactory.setChannelListeners(listeners);
        }
        //Rabbit client底层会创建com.rabbitmq.client.impl.AMQConnection.heartbeatExecutor只有一个线程的包活线程线程池，
        // 详情请见com.rabbitmq.client.impl.HeartbeatSender.createExecutorIfNecessary，ThreadFactory采用的是默认的，
        // 可以通过采用下面的Factory是标识具体的线程池默认产生的线程会采用这个方法
        connectionFactory.setConnectionThreadFactory(new ThreadFactory() {
            public final AtomicInteger id = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, MessageFormat.format("amqp-heart-{0}", id.getAndIncrement()));
            }
        });
        //默认的线程池和下面的差不多，在com.rabbitmq.client.impl.ConsumerWorkService构造方法中，大小为处理器的两倍
        //如果采用DirectRabbitListenerContainerFactory需要重写，增加线程池的中的线程数量,具体看你的应用是计算密集还是io密集，
        // io越密集，线程数设置越大
        final ExecutorService executorService = Executors.newFixedThreadPool(5, new ThreadFactory() {
            public final AtomicInteger id = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, MessageFormat.format("amqp-client-{0}", id.getAndIncrement()));
            }
        });

        connectionFactory.setExecutor(executorService);
        return connectionFactory;
    }


}
