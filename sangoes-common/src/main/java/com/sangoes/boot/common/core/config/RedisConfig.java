package com.sangoes.boot.common.core.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Copyright (c) 2018 Redis配置
 *
 * @author jerrychir
 * @date 2018/10/29 9:47 PM
 */
@Configuration
public class RedisConfig {
    // @Bean
    // public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory
    // redisConnectionFactory) {
    // StringRedisTemplate redisTemplate = new
    // StringRedisTemplate(redisConnectionFactory);
    // Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new
    // Jackson2JsonRedisSerializer(Object.class);
    // /**
    // * 通用的序列化和反序列化设置
    // * ObjectMapper类是Jackson库的主要类。它提供一些功能将转换成Java对象匹配JSON结构，反之亦然。
    // */
    // ObjectMapper objectMapper = new ObjectMapper();
    // objectMapper.setVisibility(PropertyAccessor.ALL,
    // JsonAutoDetect.Visibility.ANY);
    // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

    // jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    // redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    // redisTemplate.afterPropertiesSet();
    // return redisTemplate;
    // }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    // redis缓存和EhCache缓存不能同时存在
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.prefixKeysWith("sangoes").entryTtl(Duration.ofMinutes(5)) // 设置缓存的默认过期时间，也是使用Duration设置
                .disableCachingNullValues(); // 不缓存空值
        // // 设置一个初始化的缓存空间set集合
        // Set<String> cacheNames = new HashSet<>();
        // cacheNames.add("timeGroup");
        // cacheNames.add("user");
        // // 对每个缓存空间应用不同的配置
        // Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        // configMap.put("timeGroup", config);
        // configMap.put("user", config.entryTtl(Duration.ofSeconds(120)));
        // RedisCacheManager
        // RedisCacheManager cacheManager =
        // RedisCacheManager.builder(redisConnectionFactory) //
        // 使用自定义的缓存配置初始化一个cacheManager
        // .initialCacheNames(cacheNames) // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
        // .withInitialCacheConfigurations(configMap).build();
        RedisCacheManager cacheManager = RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory)).cacheDefaults(config)
                .build();
        return cacheManager;
    }
}
