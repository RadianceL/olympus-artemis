package com.olympus.base.utils.web.https;

import com.olympus.base.utils.web.HttpCustomClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Map;

/**
 * 双向HTTPS认证请求
 * since 10/25/22
 *
 * @author eddie
 */
public class HttpsDualClient extends HttpCustomClient {
    /**
     * HTTPS协议
     */
    private static final String[] HTTPS_PROTOCOL = new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"};
    /**
     * SSL连接认证工厂
     */
    private final SSLConnectionSocketFactory sslConnectionSocketFactory;

    private HttpsDualClient(SSLConnectionSocketFactory sslConnectionSocketFactory) throws Exception {
        this.sslConnectionSocketFactory = sslConnectionSocketFactory;
    }

    /**
     * 获取HTTPS双向认证实例
     * @param trustStoreFile            JKS文件地址
     * @param trustStorePassword        JKS文件密码
     * @return                          双向HTTPS认证客户端
     */
    public static HttpsDualClient getHttpsDualClientInstance(String trustStoreFile, String trustStorePassword) throws Exception{
        KeyStore keyStore = KeyStore.getInstance("jks");
        try (InputStream in = new FileInputStream(trustStoreFile)) {
            keyStore.load(in, trustStorePassword.toCharArray());
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                    .loadKeyMaterial(keyStore, trustStorePassword.toCharArray())
                    .build();
            HostnameVerifier verifier = (s, sslSession) -> true;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslcontext,
                    HTTPS_PROTOCOL,
                    null,
                    verifier
            );
            return new HttpsDualClient(sslConnectionSocketFactory);
        }
    }

    /**
     * 双向Https Get请求
     *
     * @param requestUrl 请求Url
     * @param paramMap   请求参数
     * @return 返回值
     */
    public String httpsGetRequest(String requestUrl, Map<String, String> paramMap) throws Exception {
        return httpsGetRequest(requestUrl, null, paramMap);
    }


    /**
     * 双向Https Post请求
     *
     * @param requestUrl  请求Url
     * @param requestBody 请求体
     * @return 返回值
     */
    public String httpsPostRequest(String requestUrl, String requestBody) throws Exception {
        return httpsPostRequest(requestUrl, null, requestBody, StandardCharsets.UTF_8);
    }

    /**
     * 双向Https Post请求
     *
     * @param requestUrl  请求Url
     * @param requestBody 请求体
     * @param charset     字符集编码
     * @return 返回值
     */
    public String httpsPostRequest(String requestUrl, String requestBody, String charset) throws Exception {
        return httpsPostRequest(requestUrl, null, requestBody, Charset.forName(charset));
    }

    /**
     * 双向Https Get请求
     *
     * @param requestUrl 请求Url
     * @param headerMap  请求头
     * @param paramMap   请求参数
     * @return 返回值
     */
    public String httpsGetRequest(String requestUrl, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception {
        StringBuilder resultMsg;
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(getPoolingHttpClientConnectionManagerInstance())
                .build()) {

            HttpGet httpGet = getHttpGet(requestUrl, headerMap, paramMap);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            resultMsg = send(response);
        }
        return resultMsg.toString();
    }

    /**
     * 双向Https Post请求
     */
    public String httpsPostRequest(String requestUrl, Map<String, String> headerMap, String requestBody, Charset charset) throws Exception {
        StringBuilder resultMsg;
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(getPoolingHttpClientConnectionManagerInstance())
                .build()) {
            HttpPost httpPost = getHttpPost(requestUrl, headerMap);
            StringEntity stringEntity = new StringEntity(requestBody, charset);
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            resultMsg = send(response);
        }
        return resultMsg.toString();
    }

    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManagerInstance() {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofMinutes(1))
                        .build())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofMinutes(1))
                        .setConnectTimeout(Timeout.ofMinutes(1))
                        .setTimeToLive(TimeValue.ofMinutes(10))
                        .build())
                .build();
    }
}
