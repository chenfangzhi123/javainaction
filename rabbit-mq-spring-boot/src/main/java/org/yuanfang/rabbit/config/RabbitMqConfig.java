package org.yuanfang.rabbit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.yuanfang.rabbit.common.constant.SpringConstant;

/**
 * @Author: chenfangzhi
 * @Description: 普通的spring配置见 {@link SpringOldConfig}
 * @Date: 2018/9/19-21:45
 * @ModifiedBy:
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    /**
     * 默认采用了java的序列化，性能比较低，而且阅读不友好
     *
     * @return
     */
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Direct类型的Container，性能好且更简单
     *
     * @param connectionFactory
     *
     * @return
     */
    @Bean
    DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        final DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory = new DirectRabbitListenerContainerFactory();
        directRabbitListenerContainerFactory.setConsumersPerQueue(Runtime.getRuntime().availableProcessors());
        directRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        directRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        directRabbitListenerContainerFactory.setConsumersPerQueue(10);
        return directRabbitListenerContainerFactory;
    }

    /**
     * 显示声明MessageListenerContainer
     *
     * @param connectionFactory
     *
     * @return
     */
    @Bean
    SimpleRabbitListenerContainerFactory container(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        //错误的回调，还有个org.springframework.amqp.rabbit.listener.RabbitListenerErrorHandler
        simpleRabbitListenerContainerFactory.setErrorHandler(t -> log.error("listener error!", t));
        simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        simpleRabbitListenerContainerFactory.setTaskExecutor(getTaskExecutor());
        return simpleRabbitListenerContainerFactory;
    }

    /**
     * 注意{@link SimpleAsyncTaskExecutor}每次执行一个任务都会新建一个线程，对于生命周期很短的任务不要使用这个线程池，
     * 这里的消费者线程生命周期直到{@link SimpleMessageListenerContainer}停止所以没有适合这个场景
     *
     * @return
     */
    @Bean
    public SimpleAsyncTaskExecutor getTaskExecutor() {
        return new SimpleAsyncTaskExecutor("amqp-consumer-");
    }

    /**
     * 和上面的唯一区别是需要手动确认
     * 默认是自动模式，container会根据返回值或是抛出异常来确认消息
     *
     * @param connectionFactory
     *
     * @return
     */
    @Bean
    SimpleRabbitListenerContainerFactory containerWithConfirm(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        simpleRabbitListenerContainerFactory.setTaskExecutor(getTaskExecutor());
        return simpleRabbitListenerContainerFactory;
    }

    @Bean
    @Profile(SpringConstant.CONSUMER_NONE_PROFILE)
    SimpleRabbitListenerContainerFactory containerWithNone(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.NONE);
        //这里每个客户端预取一条，是为了更好的复现问题。将消费者设置为RabbitMQ的自动模式时，这个参数没有作用了
        simpleRabbitListenerContainerFactory.setPrefetchCount(1);
        simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        simpleRabbitListenerContainerFactory.setTaskExecutor(getTaskExecutor());
        return simpleRabbitListenerContainerFactory;
    }
}
