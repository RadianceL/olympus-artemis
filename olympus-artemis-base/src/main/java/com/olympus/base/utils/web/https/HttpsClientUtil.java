package com.olympus.base.utils.web.https;

import com.olympus.base.utils.support.utils.InputStreamUtils;
import com.olympus.base.utils.web.HttpCustomClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网络工具类
 * 2019/10/5
 *
 * @author eddielee
 */
public class HttpsClientUtil extends HttpCustomClient {

    /**
     * 单向Http Get请求
     */
    public static String singletonHttpsGetRequest(String requestUrl, Map<String, String> paramMap) throws Exception {
        return singletonHttpsGetRequest(requestUrl, null, paramMap);
    }

    /**
     * 单向Https Get请求
     */
    public static String singletonHttpsGetRequest(String requestUrl, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception {
        StringBuilder resultSb;
        try (CloseableHttpClient httpClient = getSingletonHttpsClient()) {
            HttpGet httpGet = getHttpGet(requestUrl, headerMap, paramMap);
            resultSb = httpClient.execute(httpGet, HttpCustomClient.HTTP_CLIENT_RESPONSE_HANDLER);
        }
        return resultSb.toString();
    }

    /**
     * 单向Https Get请求
     */
    public static byte[] singletonHttpsGetForByte(String requestUrl, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception {
        byte[] resultSb;
        try (CloseableHttpClient httpClient = getSingletonHttpsClient()) {
            HttpGet httpGet = getHttpGet(requestUrl, headerMap, paramMap);
            resultSb = httpClient.execute(httpGet, HttpCustomClient.HTTP_CLIENT_RESPONSE_BYTE_HANDLER);
        }
        return resultSb;
    }

    /**
     * 下载
     */
    public static byte[] singletonHttpsGetForByte(String requestUrl) throws Exception {
        byte[] resultSb;
        try (CloseableHttpClient httpClient = getSingletonHttpsClient()) {
            HttpGet httpGet = new HttpGet(requestUrl);
            httpGet.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            resultSb = httpClient.execute(httpGet, HttpCustomClient.HTTP_CLIENT_RESPONSE_BYTE_HANDLER);
        }
        return resultSb;
    }

    /**
     * 单向Https Post请求
     */
    public static String singletonHttpsPostRequest(String requestUrl, Map<String, String> headerMap, String param) throws Exception {
        StringBuilder resultSb;
        try (CloseableHttpClient httpClient = getSingletonHttpsClient()) {
            HttpPost httpPost = getHttpPost(requestUrl, headerMap);

            StringEntity stringEntity = new StringEntity(param, StandardCharsets.UTF_8);
            httpPost.setEntity(stringEntity);
            resultSb = httpClient.execute(httpPost, HttpCustomClient.HTTP_CLIENT_RESPONSE_HANDLER);
        }
        return resultSb.toString();
    }

    public static String singletonHttpsPost(String requestUrl, Map<String, ? extends Object> params, String charSet) throws Exception {
        return singletonHttpsPost(requestUrl, params, Charset.forName(charSet), 1000);
    }

    /**
     * 单向Https Post请求
     */
    public static String singletonHttpsPost(String requestUrl, Map<String, ? extends Object> params, Charset charSet, Integer timeout) throws Exception {
        StringBuilder resultSb;
        try (CloseableHttpClient httpClient = getSingletonHttpsClient()) {
            HttpPost httpPost = new HttpPost(requestUrl);

            List<NameValuePair> paris = new ArrayList<>();
            for (String key : params.keySet()) {
                String value = String.valueOf(params.get(key));
                paris.add(new BasicNameValuePair(key, StringUtils.strip(value)));
            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(Timeout.ofSeconds(timeout))
                    .setResponseTimeout(Timeout.ofSeconds(timeout)).build();
            httpPost.setConfig(requestConfig);

            httpPost.setEntity(new UrlEncodedFormEntity(paris, charSet));
            resultSb = httpClient.execute(httpPost, HttpCustomClient.HTTP_CLIENT_RESPONSE_HANDLER);
        }
        return resultSb.toString();
    }

    /**
     * http && 单向https
     */
    private static CloseableHttpClient getSingletonHttpsClient() {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        ConnectionSocketFactory socketFactory = new PlainConnectionSocketFactory();
        registryBuilder.register("http", socketFactory);
        //指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            TrustStrategy anyTrustStrategy = (x509Certificates, s) -> true;
            HostnameVerifier verifier = (s, sslSession) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, anyTrustStrategy).build();
            LayeredConnectionSocketFactory layeredConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, verifier);
            registryBuilder.register("https", layeredConnectionSocketFactory);

        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        //设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        return HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();
    }
}
