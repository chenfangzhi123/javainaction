package org.yuanfang.rabbit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.SendRetryContextAccessor;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.retry.RetryContext;

/**
 * @Author: chenfangzhi
 * @Description: 普通的spring配置见 {@link SpringOldConfig}
 * @Date: 2018/9/19-21:45
 * @ModifiedBy:
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    private static Object recover(RetryContext ctx) {
        Message failed = SendRetryContextAccessor.getMessage(ctx);
        Address message = SendRetryContextAccessor.getAddress(ctx);
        Throwable t = ctx.getLastThrowable();
        //一般是记录日志和投递到别的队列
        log.error("reply error,message:{},reply to:{}", failed, message, t);
        return null;
    }

    // @Bean
    // @Primary
    // RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    //     RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    //     rabbitTemplate.setMessageConverter(messageConverter());
    //     return rabbitTemplate;
    // }

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
        simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        simpleRabbitListenerContainerFactory.setTaskExecutor(getTaskExecutor());
        //测试消费者回复消息失败时的处理逻辑
        //一般是记录日志和投递到别的队列
        // todoBychenfangzhi ----2018/9/23 1:10------>
        simpleRabbitListenerContainerFactory.setReplyRecoveryCallback(RabbitMqConfig::recover);
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
        // todoBychenfangzhi ----2018/9/23 1:20------>测试AcknowledgeMode.NONE狂发消息的情况
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        simpleRabbitListenerContainerFactory.setTaskExecutor(getTaskExecutor());
        simpleRabbitListenerContainerFactory.setReplyRecoveryCallback(RabbitMqConfig::recover);
        return simpleRabbitListenerContainerFactory;
    }
}
