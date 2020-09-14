package com.el.common.web.access.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

/**
 * 路由过滤器
 * 方法间信息传递使用ThreadLocal
 * since 2020/3/8
 *
 * @author eddie
 */
@Slf4j
public abstract class AbstractExtendVerify implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            if (doPreFilterHandler(servletRequest, servletResponse, filterChain)) {
                filterChain.doFilter(servletRequest, servletResponse);
                doAfterHandlerSuccess(servletRequest, servletResponse, filterChain);
            }
        }finally {
            doFinalHandler(servletRequest, servletResponse, filterChain);
        }

    }

    /**
     * servlet初始化时执行
     * @param servletRequest        请求
     * @param servletResponse       返回
     * @param filterChain           chain
     * @return                      是否执行控制器
     */
    public abstract boolean doPreFilterHandler(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain);

    /**
     * 请求成功时
     * @param servletRequest        请求
     * @param servletResponse       返回
     * @param filterChain           chain
     */
    public abstract void doAfterHandlerSuccess(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain);

    /**
     * 发生异常时一定会执行此控制器
     * @param servletRequest        请求
     * @param servletResponse       返回
     * @param filterChain           chain
     */
    public abstract void doFinalHandler(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain);

}
