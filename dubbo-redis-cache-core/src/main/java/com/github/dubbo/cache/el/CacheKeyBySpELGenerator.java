package com.github.dubbo.cache.el;

import com.github.dubbo.cache.CacheKeyGenerator;
import com.github.dubbo.cache.CacheMetadata;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * spring el key生成器
 */
public class CacheKeyBySpELGenerator implements CacheKeyGenerator {

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
        if (StringUtils.isBlank(cacheMetadata.getKey())) {
            return null;
        }
        ExpressionValueHolder expressionValueHolder = cache.get(cacheMetadata.getMethod());
        if (expressionValueHolder == null) {
            Expression expression = spelExpressionParser.parseExpression(cacheMetadata.getKey(), new TemplateParserContext());
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(cacheMetadata.getMethod());
            expressionValueHolder = new ExpressionValueHolder(expression, parameterNames);
            cache.putIfAbsent(cacheMetadata.getMethod(), expressionValueHolder);
        }
        CacheEvaluationContext context = new CacheEvaluationContext(cacheMetadata, cacheMetadata.getMethod(), args, expressionValueHolder.parameterNames);
        return expressionValueHolder.expression.getValue(context);
    }

    private static class ExpressionValueHolder {

        private final Expression expression;
        private final String[] parameterNames;

        public ExpressionValueHolder(Expression expression, String[] parameterNames) {
            this.expression = expression;
            this.parameterNames = parameterNames;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ExpressionValueHolder that = (ExpressionValueHolder) o;
            return expression.equals(that.expression) && Arrays.equals(parameterNames, that.parameterNames);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expression, parameterNames);
        }
    }
}
