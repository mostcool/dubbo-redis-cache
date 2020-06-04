package com.github.dubbo.cache;

import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

@SPI("redis")
public interface CacheFactory {

    @Adaptive("dubboCacheFactory")
    Cache getCache(Invoker<?> invoker, Invocation invocation, CacheMetadata cacheMetadata);
}
