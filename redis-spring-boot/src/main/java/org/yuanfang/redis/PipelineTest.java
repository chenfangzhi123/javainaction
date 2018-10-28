package org.yuanfang.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/28-18:40
 * @ModifiedBy:
 */
@Component
public class PipelineTest {
    @Autowired
    StringRedisTemplate redisTemplate;

    @PostConstruct
    public void test() {
        List<Object> objects = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                for (int i = 0; i < 100; i++) {
                    operations.opsForList().leftPush("list", String.valueOf(i));
                }
                //这个返回值没有作用，必须返回空，不然会报InvalidDataAccessApiUsageException异常
                return null;
            }
        });
        System.out.println(objects);
    }
}
