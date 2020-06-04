package com.github.dubbo.cache;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DubboConsumerCache {

    String cacheName() default "";

    String key();

    /**
     * cache expire time, unit:second
     */
    int expire() default 5 * 60;
}

