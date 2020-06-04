package com.github.dubbo.cache.el;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

class CacheEvaluationContext extends StandardEvaluationContext {

    private final Method method;

    private final Object[] arguments;

    private final String[] paramNames;

    CacheEvaluationContext(Object rootObject, Method method, Object[] arguments, String[] paramNames) {
        super(rootObject);
        this.method = method;
        this.arguments = arguments;
        this.paramNames = paramNames;
        loadArguments();
    }

    private void loadArguments() {
        int paramCount = method.getParameterTypes().length;
        for (int i = 0; i < paramCount; i++) {
            Object value = arguments[i];
            setVariable("p" + i, value);
            if (paramNames != null && paramNames.length > 0) {
                setVariable(paramNames[i], value);
            }
        }
    }
}
