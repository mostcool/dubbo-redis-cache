package com.github.dubbo.filter;

import com.github.dubbo.cache.Cache;
import com.github.dubbo.cache.CacheFactory;
import com.github.dubbo.cache.CacheMetadata;
import com.github.dubbo.cache.KeyGenerator;
import com.github.dubbo.cache.el.SpelKeyGenerator;
import com.github.dubbo.cache.impl.NullCache;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;

/**
 * 提供dubbo消费者直接使用缓存的能力，当缓存不存在时，再访问远程dubbo服务。
 */
@Activate(group = CONSUMER, order = Integer.MIN_VALUE + 1)
public class DubboConsumerCacheFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(DubboConsumerCacheFilter.class);

    private CacheFactory cacheFactory;

    private final KeyGenerator keyGenerator = new SpelKeyGenerator();

    public void setCacheFactory(CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        CacheMetadata cacheMetadata = CacheMetadata.build(invoker, invocation);
        if (cacheMetadata != null) {
            Cache cache = cacheFactory.getCache(invoker, invocation, cacheMetadata);
            if (cache != null && !cache.equals(NullCache.INSTANCE)) {
                Object elEvaluatedKey = keyGenerator.key(cacheMetadata, invocation.getArguments());
                if (cache.getCacheKeyValidator().isValid(invoker.getUrl(), invocation, cacheMetadata, elEvaluatedKey)) {
                    Object value = cache.get(elEvaluatedKey);
                    if (value != null) {
                        logger.info(String.format("@DubboConsumerCache hit, service = %s, cachePrefix = %s, elEvaluatedKey = %s", cacheMetadata.getMethodFullName(), cacheMetadata.getCachePrefix(), elEvaluatedKey));
                        return AsyncRpcResult.newDefaultAsyncResult(value, invocation);
                    }
                    Result result = invoker.invoke(invocation);
                    if (!result.hasException()) {
                        if (cache.getCacheValueValidator().isValid(invoker.getUrl(), invocation, cacheMetadata, result.getValue())) {
                            cache.put(elEvaluatedKey, result.getValue());
                        }
                    }
                    return result;
                } else {
                    logger.warn(String.format("key[%s] is not support by %s", elEvaluatedKey, cache.getClass().getName()));
                }
            }
        }
        return invoker.invoke(invocation);
    }
}
