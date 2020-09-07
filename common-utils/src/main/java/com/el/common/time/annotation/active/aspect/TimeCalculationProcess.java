package com.el.common.time.annotation.active.aspect;

import com.el.common.time.annotation.TimeCalculation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


/**
 * 时间计数器注解处理 by aop <br/>
 * 2019/10/5
 *
 * @author eddielee
 */
@Slf4j
@Aspect
@Component
public class TimeCalculationProcess {

    @Pointcut("@annotation(com.el.common.time.annotation.TimeCalculation)")
    public void costTimePointCut() {
    }

    @Around("costTimePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        logCostTime(point, time);
        return result;
    }

    private void logCostTime(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        TimeCalculation annotation = signature.getMethod().getAnnotation(TimeCalculation.class);
        long slowMethodDefine = annotation.slowMethodDefine();
        if (time >= slowMethodDefine) {
            String desc = annotation.value();
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            log.info("class: {}, method: {}, desc:{}, cost: [{}]ms", className, methodName, desc, time);
        }
    }
}