package com.github.dubbo.cache.impl;

import com.github.dubbo.cache.AbstractCache;
import com.github.dubbo.cache.CacheMetadata;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisCache extends AbstractCache {

    private final RedisTemplate redisTemplate;
    private final long expire;
    private final String keyPrefix;

    public RedisCache(RedisTemplate redisTemplate, CacheMetadata cacheMetadata) {
        this.redisTemplate = redisTemplate;
        this.expire = cacheMetadata.getExpire();
        this.keyPrefix = cacheMetadata.getKeyPrefix();
    }

    @Override
    public void put(Object key, Object value) {
        redisTemplate.opsForValue().set(keyPrefix + key.toString(), value, expire, TimeUnit.SECONDS);
    }

    @Override
    public Object get(Object key) {
        return redisTemplate.opsForValue().get(keyPrefix + key.toString());
    }
}
