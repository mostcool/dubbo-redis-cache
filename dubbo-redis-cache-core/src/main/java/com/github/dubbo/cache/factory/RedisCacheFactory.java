package com.github.dubbo.cache.factory;

import com.github.dubbo.cache.AbstractCacheFactory;
import com.github.dubbo.cache.Cache;
import com.github.dubbo.cache.CacheMetadata;
import com.github.dubbo.cache.impl.RedisCache;
import org.apache.dubbo.config.spring.extension.SpringExtensionFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RedisCacheFactory extends AbstractCacheFactory {

    private final RedisTemplate redisTemplate;

    public RedisCacheFactory() {
        Optional<ApplicationContext> contextOptional = SpringExtensionFactory.getContexts().stream().filter(Objects::nonNull).findFirst();
        if (contextOptional.isPresent()) {
            ApplicationContext context = contextOptional.get();
            Map<String, RedisTemplate> beansOfType = context.getBeansOfType(RedisTemplate.class);
            if (beansOfType.isEmpty()) {
                throw new IllegalStateException("RedisTemplate is not found in spring container, @DubboConsumerCache don't work!");
            } else {
                redisTemplate = beansOfType.values().iterator().next();
            }
        } else {
            throw new IllegalStateException("non spring application, @DubboConsumerCache don't work!");
        }
    }

    @Override
    public Cache doGetCache(Invoker<?> invoker, Invocation inv, CacheMetadata cacheMetadata) {
        if (redisTemplate == null) {
            return null;
        }
        return new RedisCache(redisTemplate, cacheMetadata);
    }
}