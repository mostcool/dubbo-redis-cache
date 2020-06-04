package com.github.dubbo.cache.validator;

import com.github.dubbo.cache.CacheKeyValidator;
import com.github.dubbo.cache.CacheMetadata;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

/**
 * key不为null时可以缓存
 */
public class DefaultCacheKeyValidator implements CacheKeyValidator {

    @Override
    public boolean isValid(URL url, Invocation invocation, CacheMetadata cacheMetadata, Object elEvaluatedKey) {
        return elEvaluatedKey != null;
    }
}
