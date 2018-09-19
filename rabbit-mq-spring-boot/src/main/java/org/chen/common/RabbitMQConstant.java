package org.chen.common;

/**
 * @Author: chenfangzhi
 * @Description: 消息队列的常量类，多工程时需要发布到api包中
 * @Date: 2018/9/19-21:16
 * @ModifiedBy:
 */
public abstract class RabbitMQConstant {

    /**
     * rabbitmq前缀名
     */
    public static final String RABBITMQ_PREFIX = "rabbitmq";

    public static final String TURE_CONSTANT = "true";
    /**
     * 默认交换器名字
     */
    public static final String DEFAULT_EXCHANGE = "default_exchange";
    /**
     * 默认队列名字
     */
    public static final String DEFAULT_QUEUE = "default_queue";

    public static final String DEFAULT_KEY = "default.key";

    /**
     * 显示声明的队列名字
     */
    public static final String EXPLICIT_QUEUE = "explicit_queue";
    /**
     * 显示声明的交换器名字
     */
    public static final String EXPLICIT_EXCHANGE = "explicit_exchange";

    public final static String EXPLICIT_KEY = "explicit.key";


    public static final String DIFFERENT_DATA_TYPE_QUEUE = "different_queue";
}
