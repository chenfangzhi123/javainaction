package org.chen.consume;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/18-21:07
 * @ModifiedBy:
 */

import lombok.extern.slf4j.Slf4j;
import org.chen.common.RabbitMQConstant;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queuesToDeclare = @Queue(name = RabbitMQConstant.DIFFERENT_DATA_TYPE_QUEUE, durable = "true"))
@Slf4j
// todoBychenfangzhi ----2018/9/20 1:00------>不同的数据类型
public class Consumer2 {

    /**
     * RabbitHandler用于有多个方法时但是参数类型不能一样，否则会报错
     *
     * @param msg
     */
    @RabbitHandler
    public void process(String msg) {
        log.info("param:{msg = [" + msg + "]} info:");
    }


    @RabbitHandler
    public void processByte(byte[] msg) {
        log.info("param:{msg = [" + msg + "]} info:");
    }


}
