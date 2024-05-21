package com.zsp.zspoj.aop;

import com.zsp.zspoj.annotation.AuthCheck;
import com.zsp.zspoj.annotation.RateLimiter;
import com.zsp.zspoj.common.ErrorCode;
import com.zsp.zspoj.common.ResultUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RateInterceptor {

    @Resource
    private RedissonClient redissonClient;

    @Around("@annotation(rateLimiter)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        //获取second和number
        int second = rateLimiter.second();
        int number = rateLimiter.number();
        //获取sessionId
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String sessionId = request.getSession().getId();
        // 获取限流对象，以sessionId为key
        RRateLimiter rateLimiter1 = redissonClient.getRateLimiter(sessionId);
        // 设置限流参数，每second秒获得number次令牌
        rateLimiter1.trySetRate(RateType.OVERALL, number, second, RateIntervalUnit.SECONDS);
        //尝试获取令牌
        boolean permit = rateLimiter1.tryAcquire();
        if (!permit) {
            //也可在此处抛出异常，中断此次操作
//            return "操作过于频繁";
            return ResultUtils.error(ErrorCode.FREQUENT_ERROR, "请求过于频繁，稍后再试");
        }
        //此处执行后端登录操作
        return joinPoint.proceed();
    }
}
