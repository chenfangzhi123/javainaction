package org.yuanfang.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/28-15:57
 * @ModifiedBy:
 */
@Component
public class StringRedisTemplateTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    /**
     * springboot默认会创建一个
     */
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    RedisTemplate<String, Integer> customizedTemplate;

    @Autowired
    @Qualifier("c")
    RedisTemplate<String, String> customizedTemplate2;

    @Autowired
    @Qualifier("c2")
    RedisTemplate customizedTemplate3;

    /**
     * 可以利用对应<code>RedisTemplate</code>的bean名字直接注入
     */
    @Resource(name = "customizedStringTemplate")
    ListOperations<String, String> listOperations;

    @PostConstruct
    public void test() {
        stringRedisTemplate.opsForValue().set("1", "123");
        redisTemplate.opsForValue().set("2", 1234);
        customizedTemplate.opsForValue().set("3", 12314);
        customizedTemplate2.opsForValue().set("4", "12314");
        customizedTemplate3.opsForValue().set("4".getBytes(), "12314".getBytes());
        listOperations.leftPush("list", "listinfo");

    }

}
