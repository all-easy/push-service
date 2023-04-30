package ru.all_easy.push.telegram.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.boot.actuate.health.HealthContributorRegistry;

@Configuration
@EnableCaching
//@ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
public class RedisConfig extends CachingConfigurerSupport implements CachingConfigurer {
    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

//    @Value("${redis.host}")
//    private String host;
//
//    @Value("${redis.port}")
//    private Integer port;
//
//    @Value("${redis.expiration.timeout}")
//    private Integer expirationTimeout;
//
//    @Bean
//    public JedisConnectionFactory redisConnectionFactory() {
//        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
//        redisConnectionFactory.setHostName(host);
//        redisConnectionFactory.setPort(port);
//        redisConnectionFactory.setTimeout(10);
//        return redisConnectionFactory;
//    }
//
//    @Bean
//    public RedisTemplate<String, Set<String>> redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Set<String>> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        return redisTemplate;
//    }
//
//    @Bean
//    public CacheManager cacheManager(@Autowired RedisTemplate redisTemplate) {
//        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        cacheManager.setDefaultExpiration(expirationTimeout);
//        return cacheManager;
//    }
}

//@Configuration
//public class RedisConfig {
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
//        return template;
//    }

//    @Autowired
//    private ApplicationContext context;
//
//    @Autowired
//    private RedisConnectionFactory redisConnectionFactory;
//
//    public void reloadRedisHealthIndicator() {
//        HealthContributorRegistry registry = context.getBean(HealthContributorRegistry.class);
//        registry.unregisterContributor("redis");
//        HealthIndicator redisHealthIndicator = createRedisHealthIndicator(redisConnectionFactory);
//        registry.registerContributor("redis", redisHealthIndicator);
//    }
//
//    private HealthIndicator createRedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
//        return () -> {
//            try (RedisConnection connection = redisConnectionFactory.getConnection()) {
//                connection.ping();
//                return Health.up().build();
//            } catch (Exception ex) {
//                return Health.down().withException(ex).build();
//            }
//        };
//    }
//}