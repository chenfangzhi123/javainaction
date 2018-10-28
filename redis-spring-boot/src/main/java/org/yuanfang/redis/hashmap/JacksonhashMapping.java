package org.yuanfang.redis.hashmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;
import org.yuanfang.redis.bean.Address;
import org.yuanfang.redis.bean.Person;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/28-18:05
 * @ModifiedBy:
 */
@Component
public class JacksonhashMapping {
    /**
     * 利用Jackson2HashMapper实现redis的map和javabean的映射
     */
    private final Jackson2HashMapper hashMapper = new Jackson2HashMapper(false);


    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    @Qualifier("c")
    RedisTemplate redisTemplate;

    public static Person getPerson() {
        Address address = new Address("中国", "浙江");
        return new Person().setAddress(address).setFirstname("chen").setLastname("fangzhi");
    }

    @PostConstruct
    public void test() throws JsonProcessingException {
        Person lastname = getPerson();
        Map<String, Object> stringObjectMap = hashMapper.toHash(lastname);
        redisTemplate.opsForHash().putAll("hash.test", stringObjectMap);
        Person o = (Person) hashMapper.fromHash(redisTemplate.opsForHash().entries("hash.test"));
        System.out.println(mapper.writeValueAsString(o));

    }
}
