package ru.all_easy.push.telegram.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Set;

public class RedisCacheErrorHandler implements CacheErrorHandler {
//    This is a possible way to invalidate all cache
//    @Autowired
//    RedisCacheManager cacheManager;
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
//        Set<String> cacheNames = (Set<String>) cacheManager.getCacheNames();
//        for (String cacheName : cacheNames) {
//            cacheManager.getCache(cacheName).clear();
//        }
        System.out.println(">>> Unable to get from cache " + cache.getName() + " : " + exception.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        System.out.println(">>> Unable to put into cache " + cache.getName() + " : " + exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        System.out.println(">>> Unable to evict from cache " + cache.getName() + " : " + exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        System.out.println(">>> Unable to clean cache " + cache.getName() + " : " + exception.getMessage());
    }
}
