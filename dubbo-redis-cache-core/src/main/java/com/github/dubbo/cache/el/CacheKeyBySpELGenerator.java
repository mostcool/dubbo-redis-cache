package com.github.dubbo.cache.el;

import com.github.dubbo.cache.CacheKeyGenerator;
import com.github.dubbo.cache.CacheMetadata;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * spring SpEL key生成器
 */
public class CacheKeyBySpELGenerator implements CacheKeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CacheKeyBySpELGenerator.class);

    private final SpelExpressionParser spelExpressionParser;
    private final ParameterNameDiscoverer parameterNameDiscoverer;
    private final ConcurrentMap<Method, ExpressionValueHolder> cache;

    public CacheKeyBySpELGenerator() {
        SpelParserConfiguration spelParserConfiguration = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, null);
        spelExpressionParser = new SpelExpressionParser(spelParserConfiguration);
        parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        cache = new ConcurrentHashMap<>();
    }

    @Override
    public Object key(CacheMetadata cacheMetadata, Object[] args) {
        try {
            if (StringUtils.isBlank(cacheMetadata.getKey())) {
                return null;
            }
            ExpressionValueHolder expressionValueHolder = cache.get(cacheMetadata.getMethod());
            if (expressionValueHolder == null) {
                Expression expression = spelExpressionParser.parseExpression(cacheMetadata.getKey());
                String[] parameterNames = parameterNameDiscoverer.getParameterNames(cacheMetadata.getMethod());
                expressionValueHolder = new ExpressionValueHolder(expression, parameterNames);
                cache.putIfAbsent(cacheMetadata.getMethod(), expressionValueHolder);
            }
            CacheEvaluationContext context = new CacheEvaluationContext(cacheMetadata, cacheMetadata.getMethod(), args, expressionValueHolder.parameterNames);
            return expressionValueHolder.expression.getValue(context);
        } catch (Exception e) {
            logger.warn("spring el parse failure", e);
        }
        return null;
    }

    private static class ExpressionValueHolder {

        private final Expression expression;
        private final String[] parameterNames;

        public ExpressionValueHolder(Expression expression, String[] parameterNames) {
            this.expression = expression;
            this.parameterNames = parameterNames;
        }
    }
}
