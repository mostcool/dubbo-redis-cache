package com.github.dubbo.cache.validator;

import com.github.dubbo.cache.CacheMetadata;
import com.github.dubbo.cache.CacheValueValidator;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * value不为null，并且实现了java.io.Serializable时可以缓存
 */
public class DefaultCacheValueValidator implements CacheValueValidator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCacheValueValidator.class);

    @Override
    public boolean isValid(URL url, Invocation invocation, CacheMetadata cacheMetadata, Object value) {
        if (value != null) {
            if (value instanceof Serializable) {
                return true;
            } else {
                logger.warn("{}未实现java.io.Serializable，不能序列化", value);
            }
        }
        return false;
    }
}
