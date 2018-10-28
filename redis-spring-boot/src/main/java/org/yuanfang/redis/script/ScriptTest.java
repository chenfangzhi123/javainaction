package org.yuanfang.redis.script;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/28-19:18
 * @ModifiedBy:
 */
public class ScriptTest {
    /**
     * 这个bean需要配置成单例的，每次调用时不用重复计算sha1
     *
     * @return
     * @throws IOException
     */
    @Bean
    public RedisScript<Boolean> script() throws IOException {
        ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("META-INF/scripts/checkandset.lua"));
        // 内部用的是DefaultRedisScript，
        return RedisScript.of(scriptSource.getScriptAsString(), Boolean.class);
    }
}
