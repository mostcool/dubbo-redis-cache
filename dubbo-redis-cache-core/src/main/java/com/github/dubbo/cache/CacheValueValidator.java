package com.github.dubbo.cache;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invocation;

@SPI("default")
public interface CacheValueValidator {

    /**
     * 检查value是否可以缓存
     */
    @Adaptive("cacheValueValidator")
    boolean isValid(URL url, Invocation invocation, CacheMetadata cacheMetadata, Object value);
}

