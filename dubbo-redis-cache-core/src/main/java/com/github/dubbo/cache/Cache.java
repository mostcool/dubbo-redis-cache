package com.github.dubbo.cache;

public interface Cache {

    void put(Object key, Object value);

    Object get(Object key);

    CacheValueValidator getCacheValueValidator();

    CacheKeyValidator getCacheKeyValidator();
}
