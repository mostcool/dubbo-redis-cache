package com.github.dubbo.cache;

/**
 * key生成器
 */
public interface KeyGenerator {

    Object key(CacheMetadata cacheMetadata, Object[] args);
}
