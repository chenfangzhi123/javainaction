package org.chen.config;

import lombok.extern.slf4j.Slf4j;
import org.chen.common.RabbitMQConstant;
import org.chen.vo.Message;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * @Author: chenfangzhi
 * @Description: 这里的配置就是普通spring的配置方法，在启动类加上@EnableRabbit,
 * springBoot帮你自动配置了连接工厂和SimpleMessageListenerContainer，这里不用所以注释了
 * @Date: 2018/9/19-21:53
 * @ModifiedBy:
 */
@Profile("springold")
@Configuration
@Slf4j
public class SpringOldConfig {

    @Bean
    @Qualifier(RabbitMQConstant.EXPLICIT_QUEUE)
    Queue queue() {
        return new Queue(RabbitMQConstant.EXPLICIT_QUEUE, true);
    }

    /**
     * 下面这些虽然可以通过消费者定义，但建议生产者至少要定义交换器。对于重要的消息生产者也去定义队列，因为消息匹配不到队列时便会丢失
     *
     * @return
     */
    @Bean
    @Qualifier(RabbitMQConstant.EXPLICIT_EXCHANGE)
    TopicExchange exchange() {
        return new TopicExchange(RabbitMQConstant.EXPLICIT_EXCHANGE);
    }

    @Bean
    Binding binding(@Qualifier(RabbitMQConstant.EXPLICIT_EXCHANGE) TopicExchange exchange,
                    @Qualifier(RabbitMQConstant.EXPLICIT_QUEUE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitMQConstant.EXPLICIT_KEY);
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
        return connectionFactory;
    }
// todoBychenfangzhi ----2018/9/20 0:57------>
    // /**
    //  * 显示的声明MessageListenerContainer
    //  *
    //  * @param connectionFactory
    //  * @return
    //  */
    // @Bean
    // SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListener messageListener) {
    //     SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    //     container.setConnectionFactory(connectionFactory);
    //     container.setMessageListener(messageListener);
    //     container.setQueueNames(RabbitMQConstant.EXPLICIT_QUEUE);
    //     return container;
    // }

    /**
     * 支持@Value用法
     *
     * @return
     */
    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("application.yml"));
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "process");
    }

    @Component
    public class Receiver {
        public void process(Message message) {
            log.info("old receive param:{message = [" + message + "]} info:");
        }
    }

}
