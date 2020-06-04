package com.github.dubbo.cache;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class CacheMetadata {

    private static final Logger logger = LoggerFactory.getLogger(CacheMetadata.class);

    private static final String DELIMITER = ":";
    private static final String CACHE_PREFIX_CONTAIN_GROUP = "cachePrefixContainGroup";
    private static final String CACHE_PREFIX_CONTAIN_VERSION = "cachePrefixContainVersion";

    private Method method;
    private Class<?>[] parameterTypes;
    private DubboConsumerCache dubboConsumerCache;
    private Class targetClass;
    private String group;
    private String version;

    public static CacheMetadata build(Invoker<?> invoker, Invocation inv) {
        try {
            Class interf = invoker.getInterface();
            String methodName = inv.getMethodName();
            Class[] argsClass = inv.getParameterTypes();
            Method method = interf.getDeclaredMethod(methodName, argsClass);
            DubboConsumerCache dubboCache = method.getAnnotation(DubboConsumerCache.class);
            if (dubboCache == null) {
                return null;
            }

            CacheMetadata cacheMetadata = new CacheMetadata();
            cacheMetadata.setMethod(method);
            cacheMetadata.setDubboConsumerCache(dubboCache);
            cacheMetadata.setParameterTypes(argsClass);
            cacheMetadata.setTargetClass(interf);

            boolean cachePrefixContainGroup = invoker.getUrl().getParameter(CACHE_PREFIX_CONTAIN_GROUP, Boolean.FALSE);
            if (cachePrefixContainGroup) {
                String group = invoker.getUrl().getParameter(CommonConstants.GROUP_KEY);
                cacheMetadata.setGroup(group);
            }

            boolean cachePrefixContainVersion = invoker.getUrl().getParameter(CACHE_PREFIX_CONTAIN_VERSION, Boolean.FALSE);
            if (cachePrefixContainVersion) {
                String version = invoker.getUrl().getParameter(CommonConstants.VERSION_KEY);
                cacheMetadata.setVersion(version);
            }
            return cacheMetadata;
        } catch (Exception e) {
            logger.warn("build cacheMeta failure", e);
            return null;
        }
    }

    public String getCachePrefix() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isBlank(dubboConsumerCache.cacheName())) {
            sb.append(dubboConsumerCache.cacheName());
            sb.append(DELIMITER);
        }
        if (!StringUtils.isBlank(group)) {
            sb.append(group);
            sb.append(DELIMITER);
        }
        if (!StringUtils.isBlank(version)) {
            sb.append(version);
            sb.append(DELIMITER);
        }
        return sb.toString();
    }

    public String getMethodFullName() {
        return this.targetClass.getSimpleName() + '#' + method.getName();
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public DubboConsumerCache getDubboConsumerCache() {
        return dubboConsumerCache;
    }

    public void setDubboConsumerCache(DubboConsumerCache dubboConsumerCache) {
        this.dubboConsumerCache = dubboConsumerCache;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
