package com.github.dubbo.cache;

/**
 * key生成器
 */
public interface CacheKeyGenerator {

    Object key(CacheMetadata cacheMetadata, Object[] args);
}
