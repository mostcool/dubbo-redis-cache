package com.github.dubbo.cache.factory;

import com.github.dubbo.cache.AbstractCacheFactory;
import com.github.dubbo.cache.Cache;
import com.github.dubbo.cache.CacheMetadata;
import com.github.dubbo.cache.impl.RedisCache;
import org.apache.dubbo.config.spring.extension.SpringExtensionFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RedisCacheFactory extends AbstractCacheFactory {

    private final static Logger logger = LoggerFactory.getLogger(RedisCacheFactory.class);

    private RedisTemplate redisTemplate;

    public RedisCacheFactory() {
        try {
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
                logger.warn("non spring application, @DubboConsumerCache don't work!");
            }
        } catch (Exception e) {
            logger.warn("dubbo consumer cache failure", e);
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