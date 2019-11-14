package com.liferunner.api.aspect;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * CommonLogAspect for : AOP切面实现日志确认
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/11
 */
@Component
@Aspect
@Slf4j
public class CommonLogAspect {

    @Around("execution(* com.liferunner.api.controller..*.*(..))")
    public Object recordLogTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("----------- {}.{} process log time started.---------------",
                proceedingJoinPoint.getTarget().getClass(),
                proceedingJoinPoint.getSignature().getName());

        val startTime = System.currentTimeMillis();
        //一定要将处理结果返回给原线程，否则会造成程序处理正常，但是数据请求返回结果为空！！！
        Object result = proceedingJoinPoint.proceed();
        val afterTime = System.currentTimeMillis();
        if (afterTime - startTime > 1000) {
            log.warn("cost : {}", afterTime - startTime);
        } else {
            log.info("cost : {}", afterTime - startTime);
        }

        log.info("----------- {}.{} process log time ended.---------------",
                proceedingJoinPoint.getSourceLocation().getClass(),
                proceedingJoinPoint.getSignature().getName());
        return result;
    }
}
