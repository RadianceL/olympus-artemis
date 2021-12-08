package com.el.base.utils.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 存储Cookies的请求
 *
 * @author eddie.lys
 * @since 12/8/2021
 */
public class StoreCookiesClient {

    private static final CookieStore cookieStore = new BasicCookieStore();

    public static String postRequest(String url, String param) throws IOException {
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Content-Type", "application/json");
        if (StringUtils.isNotBlank(param)) {
            StringEntity stringEntity = new StringEntity(param, StandardCharsets.UTF_8);
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse closeableHttpResponse = httpclient.execute(httpPost);
        HttpEntity entity = closeableHttpResponse.getEntity();

        StringBuilder resultMsg = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8));
        String text;
        while ((text = bufferedReader.readLine()) != null) {
            resultMsg.append(text);
        }
        EntityUtils.consume(entity);
        return resultMsg.toString();
    }

}
