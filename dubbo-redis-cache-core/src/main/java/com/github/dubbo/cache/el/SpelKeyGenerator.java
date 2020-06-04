package com.github.dubbo.cache.el;

import com.github.dubbo.cache.CacheMetadata;
import com.github.dubbo.cache.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * spring el key生成器
 */
public class SpelKeyGenerator implements KeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SpelKeyGenerator.class);

    private final SpelExpressionParser spelExpressionParser;
    private final ParameterNameDiscoverer paramDiscoverer;
    private final ConcurrentMap<Method, ExpressionValueHolder> cache;

    public SpelKeyGenerator() {
        SpelParserConfiguration spelParserConfiguration = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, null);
        spelExpressionParser = new SpelExpressionParser(spelParserConfiguration);
        paramDiscoverer = new DefaultParameterNameDiscoverer();
        cache = new ConcurrentHashMap<>();
    }

    @Override
    public Object key(CacheMetadata cacheMetadata, Object[] args) {
        try {
            if (StringUtils.isEmpty(cacheMetadata.getDubboConsumerCache().key())) {
                return null;
            }
            ExpressionValueHolder expressionValueHolder = cache.get(cacheMetadata.getMethod());
            if (expressionValueHolder == null) {
                Expression expression = spelExpressionParser.parseExpression(cacheMetadata.getDubboConsumerCache().key());
                String[] parameterNames = paramDiscoverer.getParameterNames(cacheMetadata.getMethod());
                expressionValueHolder = new ExpressionValueHolder(expression, parameterNames);
                cache.putIfAbsent(cacheMetadata.getMethod(), expressionValueHolder);
            }
            CacheEvaluationContext context = new CacheEvaluationContext(cacheMetadata, cacheMetadata.getMethod(), args, expressionValueHolder.paramNames);
            return expressionValueHolder.expression.getValue(context);
        } catch (Exception e) {
            logger.warn("spel parse failure", e);
        }
        return null;
    }

    private static class ExpressionValueHolder {

        private final Expression expression;
        private final String[] paramNames;

        public ExpressionValueHolder(Expression expression, String[] paramNames) {
            this.expression = expression;
            this.paramNames = paramNames;
        }
    }
}
