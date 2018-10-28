package org.yuanfang.redis.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/28-16:22
 * @ModifiedBy:
 */
@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Integer> customizedTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Integer> objectObjectRedisTemplate = new RedisTemplate<>();
        objectObjectRedisTemplate.setConnectionFactory(redisConnectionFactory);
        objectObjectRedisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        objectObjectRedisTemplate.afterPropertiesSet();
        return objectObjectRedisTemplate;
    }

    @Bean
    @Qualifier("c")
    public RedisTemplate<String, String> customizedStringTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> objectObjectRedisTemplate = new RedisTemplate<>();
        objectObjectRedisTemplate.setConnectionFactory(redisConnectionFactory);
        objectObjectRedisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        objectObjectRedisTemplate.afterPropertiesSet();
        return objectObjectRedisTemplate;
    }

    @Bean
    @Qualifier("c2")
    public RedisTemplate customizedStringTemplate2(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate objectObjectRedisTemplate = new RedisTemplate();
        //禁用所有序列化，只能采用字节数组的形式
        objectObjectRedisTemplate.setEnableDefaultSerializer(false);
        objectObjectRedisTemplate.setConnectionFactory(redisConnectionFactory);
        objectObjectRedisTemplate.afterPropertiesSet();
        return objectObjectRedisTemplate;
    }

}
