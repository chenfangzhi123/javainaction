package org.yuanfang.rabbit.error;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/23-1:02
 * @ModifiedBy:
 */
// todoBychenfangzhi ----2018/9/23 1:04------>使用errorHandler
public class ErrorHandler implements RabbitListenerErrorHandler {

    @Override
    public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) {
        return null;
    }
}
