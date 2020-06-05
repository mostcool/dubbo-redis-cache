package com.github.dubbo.cache.validator;

import com.github.dubbo.cache.CacheMetadata;
import com.github.dubbo.cache.CacheValueValidator;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import java.io.Serializable;

/**
 * value不为null，并且实现了java.io.Serializable时可以缓存
 */
public class DefaultCacheValueValidator implements CacheValueValidator {

    @Override
    public boolean isValid(URL url, Invocation invocation, CacheMetadata cacheMetadata, Object value) {
        return value != null && value instanceof Serializable;
    }
}
