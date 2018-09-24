package org.yuanfang.rabbit.common.constant;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/9/24-22:00
 * @ModifiedBy:
 */
public class SpringConstant {

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
    public static final String CONSUMEER_CONFIRM_PROFILE = "consumer.confirm";

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

    public static final String REPLY_PROFLLE = "reply.proflle";
}
