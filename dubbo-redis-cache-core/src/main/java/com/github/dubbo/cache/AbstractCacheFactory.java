package com.github.dubbo.cache;

import com.github.dubbo.cache.impl.NullCache;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractCacheFactory implements CacheFactory {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCacheFactory.class);

    private static final ConcurrentMap<CacheMetadata, Cache> concurrentMap = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(Invoker<?> invoker, Invocation inv, CacheMetadata cacheMetadata) {
        Cache cache;
        try {
            cache = concurrentMap.get(cacheMetadata);
            if (cache == null) {
                cache = doGetCache(invoker, inv, cacheMetadata);
                if (cache == null) {
                    cache = NullCache.INSTANCE;
                }
                concurrentMap.putIfAbsent(cacheMetadata, cache);
            }
        } catch (Exception e) {
            logger.warn("create Cache failure", e);
            cache = NullCache.INSTANCE;
        }
        return cache;
    }

    public abstract Cache doGetCache(Invoker<?> invoker, Invocation inv, CacheMetadata cacheMetadata);
}
