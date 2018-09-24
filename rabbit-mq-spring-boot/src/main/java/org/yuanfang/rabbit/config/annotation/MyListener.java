package org.yuanfang.rabbit.config.annotation;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.yuanfang.rabbit.common.constant.RabbitMQConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: chenfangzhi
 * @Description: 没有声明queue，默认是自动删除，排他的队列
 * ignoreDeclarationExceptions表示允许声明的不一致
 * arguments声明参数信息,
 * 如果默认多出要用到则可以提取出来
 * @Date: 2018/9/22-23:28
 * @ModifiedBy:
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue,
        exchange = @Exchange(value = RabbitMQConstant.DEFAULT_EXCHANGE,type = ExchangeTypes.TOPIC,
            durable = RabbitMQConstant.FALSE_CONSTANT, autoDelete = RabbitMQConstant.TURE_CONSTANT),
        key = RabbitMQConstant.DEFAULT_KEY,
        ignoreDeclarationExceptions = "true",
        arguments = @Argument(name = "x-message-ttl", value = "10000", type = "java.lang.Integer")
    )
)
public @interface MyListener {

}
