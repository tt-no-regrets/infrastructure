package com.guns21.session.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/**
 * Created by ljj on 17/5/24.
 */
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 5)
@EnableRedisHttpSession
public class HttpSessionConfig {

    /**
     * 通过header传递session ID.
     */
//    @Bean
//    public HttpSessionStrategy httpSessionStrategy() {
//        return new HeaderHttpSessionStrategy();
//    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

    @Bean
    @ConditionalOnMissingBean(RedisSerializer.class)
    public RedisSerializer springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Configuration
    public class RedisSessionConfig {

        @Value("${server.session.timeout:1800}")
        private Integer maxInactiveIntervalInSeconds;

        @Autowired
        public void setRedisOperationsSessionRepository(RedisOperationsSessionRepository redisOperationsSessionRepository) {
            redisOperationsSessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);
        }
    }
}
