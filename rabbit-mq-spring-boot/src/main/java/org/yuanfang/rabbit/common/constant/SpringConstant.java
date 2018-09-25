package org.yuanfang.rabbit.common.constant;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/24-22:00
 * @ModifiedBy:
 */
public abstract class SpringConstant {

    /**
     * 手动设置的方式
     */
    public static final String OLD_CONFIG_PROFILE = "old.config";
    /**
     * 注册channel监听器的方式
     */
    public static final String CHANNEL_LISTENER_PROFILE = "channel.listener";
    /**
     * 发送者确认模式
     */
    public static final String PUBLISHER_CONFIRM_PROFILE = "publisher.confirm";
    /**
     * 消费者确认模式
     */
    public static final String CONSUMER_CONFIRM_PROFILE = "consumer.confirm";

    /**
     * 监听消费者错误事件
     */
    public static final String CONSUMER_LISTENER_PROFILE = "consumer.error";
    /**
     * 手动注册队列等等
     */
    public static final String PROGRAMMATICALLY_CONFIG_PROFILE = "programmatically.config";
    /**
     * direct监听容器
     */
    public static final String DIRECT_LISTENER_PROFILE = "direct.listener";
    /**
     * 多种消息类型
     */
    public static final String MULTIPART_PROFILE = "multipart";

    public static final String REPLY_PROFILE = "reply.proflle";
    /**
     * 自动确认的消费者
     */
    public static final String CONSUMER_NONE_PROFILE = "consumer.none";
    /**
     * 消息者异常时处理
     */
    public static final String CONSUMER_REQUEUE_PROFILE = "consumer.requeue";
    /**
     * 排他消费者
     */
    public static final String CONSUMER_EXCLUSIVE_PROFILE = "consumer.exclusive";
    /**
     * 带重试机制的Consumer
     */
    public static final String CONSUMER_RETRY_PROFILE = "consumer.retry";
    /**
     * 死信队列
     */
    public static final String DEAD_LETTER_PROFILE = "dead.letter";
}
