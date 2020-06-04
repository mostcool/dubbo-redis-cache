package com.github.dubbo.cache;

import org.apache.dubbo.common.extension.ExtensionLoader;

public class AbstractCache implements Cache {

    protected CacheValueValidator cacheValueValidator;
    protected CacheKeyValidator cacheKeyValidator;

    public AbstractCache() {
        this.cacheValueValidator = ExtensionLoader.getExtensionLoader(CacheValueValidator.class).getAdaptiveExtension();
        this.cacheKeyValidator = ExtensionLoader.getExtensionLoader(CacheKeyValidator.class).getAdaptiveExtension();
    }

    @Override
    public void put(Object key, Object value) {
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public CacheValueValidator getCacheValueValidator() {
        return cacheValueValidator;
    }

    @Override
    public CacheKeyValidator getCacheKeyValidator() {
        return cacheKeyValidator;
    }
}
