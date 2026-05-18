package com.demo017.student.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * =====================================================================
 * 【demo017 核心】Redis + Spring Cache 配置
 * =====================================================================
 *
 * 教学重点：
 * 1. @EnableCaching 开启声明式缓存
 * 2. 自定义 RedisTemplate，使用 Jackson 序列化（避免默认 JDK 序列化乱码）
 * 3. 配置 RedisCacheManager，统一管理缓存 TTL 和序列化
 * 4. 为后续缓存穿透/击穿/雪崩处理打下基础
 * =====================================================================
 */
@Configuration
@EnableCaching   // 关键！开启 Spring Cache 注解支持
public class RedisConfig {

    /**
     * 自定义 RedisTemplate（推荐在项目中使用）
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用 Jackson 序列化对象
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSerializer.setObjectMapper(mapper);

        // Key 使用 String 序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jacksonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jacksonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 自定义 RedisCacheManager（配合 @Cacheable 使用）
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置全局缓存过期时间
                .entryTtl(Duration.ofMinutes(30))
                // 禁用缓存 null 值（我们会在业务层自己控制空值缓存，防止穿透）
                .disableCachingNullValues()
                // Key 使用 String
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Value 使用 JSON
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(Object.class)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}