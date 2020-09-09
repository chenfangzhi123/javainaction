package org.yuanfang.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置对CacheAble的支持
 */
@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).
        maximumSize(1000));
    return cacheManager;
  }

  //guava
  //@Bean
  //public CacheManager cacheManager() {
  //  GuavaCacheManager cacheManager = new GuavaCacheManager();
  //  cacheManager.setCacheBuilder(CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).
  //      maximumSize(1000));
  //  return cacheManager;
  //}


}
