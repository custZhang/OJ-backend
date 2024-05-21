package com.zsp.zspoj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * 设置每隔second秒可以放行number次请求
     * @return
     */
    int second();

    /**
     * 设置每隔second秒可以放行number次请求
     * @return
     */
    int number();
}
