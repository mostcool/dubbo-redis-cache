package com.github.dubbo.cache.impl;

import com.github.dubbo.cache.Cache;
import com.github.dubbo.cache.CacheKeyValidator;
import com.github.dubbo.cache.CacheValueValidator;

public class NullCache implements Cache {

    public static NullCache INSTANCE = new NullCache();

    @Override
    public void put(Object key, Object value) {
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public CacheValueValidator getCacheValueValidator() {
        return (url, invocation, cacheMeta, value) -> false;
    }

    @Override
    public CacheKeyValidator getCacheKeyValidator() {
        return (url, invocation, cacheMeta, elEvaluatedKey) -> false;
    }
}
