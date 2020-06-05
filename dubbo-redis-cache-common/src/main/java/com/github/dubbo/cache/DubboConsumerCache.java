package com.github.dubbo.cache;

import java.lang.annotation.*;

/**
 * 提供dubbo消费者直接使用缓存的能力，当缓存不存在时，再访问远程dubbo服务。
 * 在dubbo服务提供者接口方法上使用的@DubboConsumerCache，由服务提供者来控制接口是否需要缓存，和缓存的控制。
 * 对于dubbo服务消费者，只需要更新jar包即可。
 * <p>
 * 1、key生成
 * key生成策略和Cacheable一致，cache key由两部分组成: cacheName, spring el表达式结果，用:分隔.
 * <pre>
 *        @DubboConsumerCache(cacheName = "test", key = "#user.name")
 *        User getUser(User user);
 * </pre>
 * User对象中有name属性，假设name值为gaokai，最终key为: test:gaokai
 * <p>
 * 如果服务有多个版本或者group，需要对多个版本和group分别设置缓存，可以设置参数：
 * cachePrefixContainGroup=true
 * cachePrefixContainVersion=true
 * 如果两个参数都设置，key由四部分组成：cacheName, group, version, spring el表达式结果, 用:分隔，建议在provider端设置此配置。
 * <p>
 * 2、控制缓存
 *
 * @DubboConsumerCache 提供了消费者可优先使用缓存，缓存的一致性由服务提供方负责，当服务提供方使用此注解后，所有的服务消费者都会使用此缓存。 1. Cache consistency requirements are not high
 * 控制缓存分为两种情况：
 * 缓存一致性要求不高，可以通过DubboConsumerCache#expire设置过期时间，默认为5分钟，单位：秒。
 * 缓存一致性要求高，服务提供方通过redisTemplate或者org.springframework.cache.annotation.CacheEvict控制缓存。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DubboConsumerCache {

    /**
     * key前缀
     */
    String prefix() default "";

    /**
     * 支持spring el表达式，p0表示第一个参数，依此类推。
     * 如果使用Java 8，并且编译器参数集-parameters，则可以使用参数名称
     */
    String key();

    /**
     * 缓存过期时间，单位：秒
     */
    int expire() default 5 * 60;
}