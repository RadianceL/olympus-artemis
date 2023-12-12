package com.olympus.base.utils.web;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.*;
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

    public static HttpClientResponseHandler<StringBuilder> HTTP_CLIENT_RESPONSE_HANDLER = classicHttpResponse -> {
        try (classicHttpResponse) {
            StringBuilder resultMsg = null;
            HttpEntity entity = classicHttpResponse.getEntity();
            if (entity != null) {
                resultMsg = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8));
                String text;
                while ((text = bufferedReader.readLine()) != null) {
                    resultMsg.append(text);
                }
            }
            if (resultMsg == null) {
                throw new RuntimeException("network response error at HttpsClientUtil send()");
            }
            return resultMsg;
        }
    };

    public static HttpClientResponseHandler<byte[]> HTTP_CLIENT_RESPONSE_BYTE_HANDLER = classicHttpResponse -> {
        try (classicHttpResponse) {
            HttpEntity entity = classicHttpResponse.getEntity();
            if (entity != null) {
                InputStream bufferedReader = entity.getContent();
                byte[] buffer = new byte[1024];
                int len;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((len = bufferedReader.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.close();
                return bos.toByteArray();
            }
        }
        throw new RuntimeException("下载文件异常");
    };
}
