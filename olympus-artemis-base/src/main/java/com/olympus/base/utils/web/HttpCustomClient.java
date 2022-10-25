package com.olympus.base.utils.web;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * http连接客户端
 * since 10/25/22
 *
 * @author eddie
 */
public class HttpCustomClient {

    /**
     * 发送请求
     */
    protected static StringBuilder send(CloseableHttpResponse response) throws IOException {
        try (response) {
            StringBuilder resultMsg = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                resultMsg = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8));
                String text;
                while ((text = bufferedReader.readLine()) != null) {
                    resultMsg.append(text);
                }
            }
            EntityUtils.consume(entity);
            if (resultMsg == null) {
                throw new RuntimeException("network response error at HttpsClientUtil send()");
            }
            return resultMsg;
        }
    }


    /**
     * 获取Http Get请求体
     */
    protected static HttpGet getHttpGet(String requestUrl, Map<String, String> headerMap, Map<String, String> paramMap) {
        StringBuilder url = new StringBuilder().append(requestUrl);
        if (!Objects.isNull(paramMap)) {
            for (String key : paramMap.keySet()) {
                url.append("&").append(key).append("=").append(paramMap.get(key));
            }
        }
        String realUrl = url.toString().replaceFirst("&", "?");
        HttpGet httpGet = new HttpGet(realUrl);
        if (!Objects.isNull(headerMap)) {
            for (String key : headerMap.keySet()) {
                httpGet.addHeader(key, headerMap.get(key));
            }
        } else {
            httpGet.addHeader("Content-Type", "application/json");
        }
        return httpGet;
    }

    /**
     * 获取Http Post请求体
     */
    protected static HttpPost getHttpPost(String requestUrl, Map<String, String> headerMap) {
        HttpPost httpPost = new HttpPost(requestUrl);
        if (!Objects.isNull(headerMap)) {
            for (String key : headerMap.keySet()) {
                httpPost.addHeader(key, headerMap.get(key));
            }
        }
        return httpPost;
    }
}
