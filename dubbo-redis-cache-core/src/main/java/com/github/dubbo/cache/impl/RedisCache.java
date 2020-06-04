package com.github.dubbo.cache.impl;

import com.github.dubbo.cache.AbstractCache;
import com.github.dubbo.cache.CacheMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

public class RedisCache extends AbstractCache {

    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

    private final RedisTemplate redisTemplate;
    private final ValueOperations<String, Object> valueOperations;

    private final long expire;
    private final String cachePrefix;

    public RedisCache(RedisTemplate redisTemplate, CacheMetadata cacheMetadata) {
        this.redisTemplate = redisTemplate;
        this.expire = cacheMetadata.getDubboConsumerCache().expire();
        this.cachePrefix = cacheMetadata.getCachePrefix();
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void put(Object key, Object value) {
        try {
            valueOperations.set(cachePrefix + key.toString(), value, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("dubbo set cache failure", e);
        }
    }

    @Override
    public Object get(Object key) {
        try {
            return valueOperations.get(cachePrefix + key.toString());
        } catch (Exception e) {
            logger.warn("dubbo get cache failure", e);
            return null;
        }
    }
}
