package com.el.common.web.access.interceptor;

import com.el.common.web.annotation.NotRequirePermissions;
import com.el.common.web.annotation.RequirePermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * since 2020/3/9
 *
 * @author eddie
 */
@Slf4j
public abstract class AbstractAuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        if (BasicErrorController.class.equals(handlerMethod.getBean().getClass())) {
            return true;
        }
        // 判断方法上是否包含该注解
        RequirePermissions methodRequirePermissions = handlerMethod.getMethodAnnotation(RequirePermissions.class);
        if (Objects.nonNull(methodRequirePermissions)) {
            return onPreHandleRequest(request, response, handler);
        }

        // 判断控制器上是否包含该注解
        Class<?> beanType = handlerMethod.getBeanType();
        RequirePermissions requirePermissions = beanType.getAnnotation(RequirePermissions.class);
        if (Objects.nonNull(requirePermissions)) {
            Method method = handlerMethod.getMethod();

            // 控制器上包含该注解 但其中一个方法不需要鉴权则忽略
            NotRequirePermissions methodAnnotation = method.getAnnotation(NotRequirePermissions.class);
            if (Objects.nonNull(methodAnnotation)) {
                return true;
            }

            return onPreHandleRequest(request, response, handler);
        }

        // 方法 && 控制器 均不包含鉴权注解 则跳过
        return true;
    }

    /**
     * 需要执行鉴权
     * @param request       请求
     * @param response      返回
     * @param handler       handle方法
     * @return              是否通过
     */
    public abstract boolean onPreHandleRequest(HttpServletRequest request, HttpServletResponse response, Object handler);

}
