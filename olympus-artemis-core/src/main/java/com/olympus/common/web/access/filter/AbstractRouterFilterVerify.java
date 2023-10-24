package com.olympus.common.web.access.filter;

import com.alibaba.fastjson2.JSON;
import com.olympus.base.utils.support.exception.ExtendRuntimeException;
import com.olympus.common.web.config.CommonWebConstants;
import com.olympus.common.web.access.FilterFailureType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * get_way路由过滤器
 * 方法间信息传递使用ThreadLocal
 * since 2020/3/8
 *
 * @author eddie
 */
@Slf4j
public abstract class AbstractRouterFilterVerify<T> implements GlobalFilter, Ordered {

    /** 准许通过的路径 */
    private static final List<String> ACROSS_PERMISSIONS = new ArrayList<>();

    private static final String ACROSS_PERMISSIONS_ALL = "*";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String url = request.getPath().value();

        if (ACROSS_PERMISSIONS.contains(ACROSS_PERMISSIONS_ALL)
                || ACROSS_PERMISSIONS.contains(url)) {
            return chain.filter(exchange.mutate().request(request).build());
        }

        HttpHeaders headers = request.getHeaders();
        ServerHttpResponse response = exchange.getResponse();
        String realIp = headers.getFirst(CommonWebConstants.X_REAL_IP);
        try {
            if (!verifyRemoteIpPermissions(realIp)) {
                return onFailureListener(request, response, FilterFailureType.REMOTE_IP);
            }
            if (!verifyRequestParameter(url, headers, request)) {
                return onFailureListener(request, response, FilterFailureType.HEADER_PARAMETER);
            }
            onSuccessListener(url, request);
        }catch (ExtendRuntimeException e) {
            return onExceptionListener(request, response, e);
        }
        return chain.filter(exchange.mutate().request(request).build());
    }

    /**
     * 校验IP是否有权限访问
     * @param xRealIp       用户真实IP
     * @return              是否有权限
     */
    public abstract boolean verifyRemoteIpPermissions(String xRealIp);

    /**
     * 校验HttpHeader参数
     * @param url           请求url
     * @param headers       HttpHeaders
     * @param request       ServerHttpRequest
     * @return              是否通过
     */
    public abstract boolean verifyRequestParameter(String url, HttpHeaders headers, ServerHttpRequest request);

    /**
     * 校验通过\
     * @param requestUrl    请求URL
     * @param request       请求参数
     */
    public abstract void onSuccessListener(String requestUrl, ServerHttpRequest request);

    /**
     * 发生错误返回
     * @param request       请求信息
     * @param failureType   错误类型
     */
    public abstract T onFailureListener(ServerHttpRequest request, FilterFailureType failureType);

    /**
     * 发生异常返回
     * @param request       请求信息
     * @param e             异常
     */
    public abstract T onExceptionListener(ServerHttpRequest request, ExtendRuntimeException e);

    /**
     * 发生错误返回
     * @param request       请求信息
     * @param response      返回封装
     * @param failureType   错误类型
     */
    public Mono<Void> onFailureListener(ServerHttpRequest request, ServerHttpResponse response, FilterFailureType failureType) {
        byte[] bits = JSON.toJSONBytes(onFailureListener(request, failureType));
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 发生异常返回
     * @param request       请求信息
     * @param response      返回封装
     * @param e             异常
     */
    public Mono<Void> onExceptionListener(ServerHttpRequest request, ServerHttpResponse response, ExtendRuntimeException e) {
        byte[] bits = JSON.toJSONBytes(onExceptionListener(request, e));
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(CommonWebConstants.HEADER_CONTENT_TYPE, CommonWebConstants.WEB_JSON_UTF8_CHARSET);
        return response.writeWith(Mono.just(buffer));
    }

    public void addAcrossPermissionsUrl(String url) {
        ACROSS_PERMISSIONS.add(url);
    }

    public void addAcrossPermissionsUrl(List<String> urls) {
        ACROSS_PERMISSIONS.addAll(urls);
    }

    public void addHttpHeader(ServerHttpRequest httpRequest, String key, String value) {
        httpRequest.mutate().header(key, value);
    }
}
