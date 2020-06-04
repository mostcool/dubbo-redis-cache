package com.github.dubbo.cache;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invocation;

@SPI("default")
public interface CacheKeyValidator {

    /**
     * 检查key是否可以缓存
     */
    @Adaptive("cacheKeyValidator")
    boolean isValid(URL url, Invocation invocation, CacheMetadata cacheMetadata, Object elEvaluatedKey);
}
