package com.el.common.web.security;

import com.alibaba.fastjson.JSON;
import com.el.common.support.exception.ExtendRuntimeException;
import com.el.common.support.utils.TraceIdUtil;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由过滤器
 * 方法间信息传递使用ThreadLocal
 * since 2020/3/8
 *
 * @author eddie
 */
@Slf4j
public abstract class AbstractRouterFilterVerify implements GlobalFilter, Ordered {

    /** 准许通过的路径 */
    private static final List<String> ACROSS_PERMISSIONS = new ArrayList<>();

    private static final String ACROSS_PERMISSIONS_ALL = "*";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String url = request.getPath().value();
        log.debug("请求URL: {} 请求方式为[{}]", url, request.getMethod());

        String traceId = TraceIdUtil.getTraceId();
        addHttpHeader(request, "traceId", traceId);

        if (ACROSS_PERMISSIONS.contains(ACROSS_PERMISSIONS_ALL)
                || ACROSS_PERMISSIONS.contains(url)) {
            return chain.filter(exchange.mutate().request(request).build());
        }

        HttpHeaders headers = request.getHeaders();
        Flux<DataBuffer> body = request.getBody();
        ServerHttpResponse response = exchange.getResponse();
        String realIp = headers.getFirst("X-Real-IP");

        try {
            if (!verifyRemoteIpPermissions(realIp)) {
                return onFailureListener(response, FilterFailureType.REMOTE_IP);
            }
            if (!verifyRequestParameter(url, headers, body)) {
                return onFailureListener(response, FilterFailureType.HEADER_PARAMETER);
            }
            onSuccessListener(request);
        }catch (ExtendRuntimeException e) {
            return onExceptionListener(response, e);
        }
        return chain.filter(exchange.mutate().request(request).build());
    }

    /**
     * 校验IP是否有权限访问
     * @param xRealIp   用户真实IP
     * @return          是否有权限
     */
    public abstract boolean verifyRemoteIpPermissions(String xRealIp);

    /**
     * 校验HttpHeader参数
     * @param url     请求url
     * @param headers HttpHeaders
     * @param body    Flux<DataBuffer>
     * @return        是否通过
     */
    public abstract boolean verifyRequestParameter(String url, HttpHeaders headers, Flux<DataBuffer> body);

    /**
     * 校验通过
     * @param request   请求参数
     */
    public abstract void onSuccessListener(ServerHttpRequest request);

    /**
     * 发生错误返回
     * @param failureType 错误类型
     * @return
     */
    public abstract Object onFailureListener(FilterFailureType failureType);

    /**
     * 发生异常返回
     * @param e 异常
     * @return
     */
    public abstract Object onExceptionListener(ExtendRuntimeException e);

    /**
     * 发生错误返回
     * @param response  HttpResponse
     */
    public Mono<Void> onFailureListener(ServerHttpResponse response, FilterFailureType failureType) {
        byte[] bits = JSON.toJSONBytes(onFailureListener(failureType));
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 发生错误返回
     * @param response  HttpResponse
     */
    public Mono<Void> onExceptionListener(ServerHttpResponse response, ExtendRuntimeException e) {
        byte[] bits = JSON.toJSONBytes(onExceptionListener(e));
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
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
