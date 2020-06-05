package com.github.dubbo.cache;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

import java.lang.reflect.Method;
import java.util.Objects;

public class CacheMetadata {

    private static final Logger logger = LoggerFactory.getLogger(CacheMetadata.class);

    private static final String DELIMITER = ":";

    private Class targetClass;
    private Method method;
    private Class<?>[] parameterTypes;
    private String version;
    private String group;

    private String prefix;
    private String key;
    private long expire;

    public static CacheMetadata build(Invoker<?> invoker, Invocation invocation) {
        try {
            String methodName = invocation.getMethodName();
            Class[] parameterTypes = invocation.getParameterTypes();
            Class targetClass = invoker.getInterface();
            Method method = targetClass.getDeclaredMethod(methodName, parameterTypes);

            DubboConsumerCache dubboConsumerCache = method.getAnnotation(DubboConsumerCache.class);
            if (dubboConsumerCache == null) {
                return null;
            }

            CacheMetadata cacheMetadata = new CacheMetadata();
            cacheMetadata.setTargetClass(targetClass);
            cacheMetadata.setMethod(method);
            cacheMetadata.setParameterTypes(parameterTypes);
            cacheMetadata.setPrefix(dubboConsumerCache.prefix());
            cacheMetadata.setKey(dubboConsumerCache.key());
            cacheMetadata.setExpire(dubboConsumerCache.expire());

            String version = invoker.getUrl().getParameter(CommonConstants.VERSION_KEY);
            if (!StringUtils.isBlank(version)) {
                cacheMetadata.setVersion(version);
            }

            String group = invoker.getUrl().getParameter(CommonConstants.GROUP_KEY);
            if (!StringUtils.isBlank(group)) {
                cacheMetadata.setGroup(group);
            }

            return cacheMetadata;
        } catch (Exception e) {
            logger.warn("build CacheMetadata failure", e);
            return null;
        }
    }

    public String getKeyPrefix() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isBlank(prefix)) {
            sb.append(prefix).append(DELIMITER);
        }
        if (!StringUtils.isBlank(version)) {
            sb.append(version).append(DELIMITER);
        }
        if (!StringUtils.isBlank(group)) {
            sb.append(group).append(DELIMITER);
        }
        return sb.toString();
    }

    public String getMethodFullName() {
        return this.targetClass.getName() + '#' + method.getName();
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheMetadata that = (CacheMetadata) o;
        return targetClass.equals(that.targetClass) &&
                method.getName().equals(that.getMethod().getName()) &&
                version.equals(that.version) &&
                group.equals(that.group) &&
                prefix.equals(that.prefix) &&
                key.equals(that.key) &&
                expire == that.expire;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetClass, method, version, group, prefix, key, expire);
    }
}
