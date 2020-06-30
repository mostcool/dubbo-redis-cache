package com.github.dubbo.cache;

import com.github.dubbo.cache.impl.NullCache;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractCacheFactory implements CacheFactory {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCacheFactory.class);

    private static final ConcurrentMap<CacheMetadata, Cache> CONCURRENT_MAP = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(Invoker<?> invoker, Invocation inv, CacheMetadata cacheMetadata) {
        Cache cache;
        try {
            cache = CONCURRENT_MAP.get(cacheMetadata);
            if (cache == null) {
                cache = doGetCache(invoker, inv, cacheMetadata);
                if (cache == null) {
                    cache = NullCache.INSTANCE;
                }
                CONCURRENT_MAP.putIfAbsent(cacheMetadata, cache);
            }
        } catch (Exception e) {
            logger.warn("create Cache failure", e);
            cache = NullCache.INSTANCE;
        }
        return cache;
    }

    protected abstract Cache doGetCache(Invoker<?> invoker, Invocation inv, CacheMetadata cacheMetadata);
}
