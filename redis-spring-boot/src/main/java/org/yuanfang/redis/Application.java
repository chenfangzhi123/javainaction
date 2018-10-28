package org.yuanfang.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/28-15:55
 * @ModifiedBy:
 */
@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class);
        System.in.read();

    }
}
