package com.el.base.utils.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 网络工具类
 * 2019/10/5
 *
 * @author eddielee
 */
public class HttpsClientUtil {

    /**
     * 获取文件流
     * HttpsClientUtil.class.getClassLoader().getResource("cert/clients.jks").getFile()
     */
    private static final String TRUST_STORE_FILE = "JKS文件地址";

    private static final String TRUST_STORE_PASSWORD = "JKS文件密码";

    private static final String[] HTTPS_PROTOCOL = new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"};

    /**
     * 双向Https Get请求
     *
     * @param requestUrl 请求Url
     * @param headerMap  请求头
     * @param paramMap   请求参数
     * @return 返回值
     * @throws Exception
     */
    public static String httpsGetRequest(String requestUrl, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception {
        StringBuilder resultMsg;
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(initConfig()).build()) {
            HttpGet httpGet = getHttpGet(requestUrl, headerMap, paramMap);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            resultMsg = send(response);
        }
        return resultMsg.toString();
    }

    /**
     * 双向Https Get请求
     *
     * @param requestUrl 请求Url
     * @param paramMap   请求参数
     * @return 返回值
     * @throws Exception
     */
    public static String httpsGetRequest(String requestUrl, Map<String, String> paramMap) throws Exception {
        return httpsGetRequest(requestUrl, null, paramMap);
    }


    /**
     * 双向Https Post请求
     *
     * @param requestUrl  请求Url
     * @param requestBody 请求体
     * @return 返回值
     * @throws Exception
     */
    public static String httpsPostRequest(String requestUrl, String requestBody) throws Exception {
        return httpsPostRequest(requestUrl, null, requestBody, StandardCharsets.UTF_8);
    }

    /**
     * 双向Https Post请求
     *
     * @param requestUrl  请求Url
     * @param requestBody 请求体
     * @param charset     字符集编码
     * @return 返回值
     * @throws Exception
     */
    public static String httpsPostRequest(String requestUrl, String requestBody, String charset) throws Exception {
        return httpsPostRequest(requestUrl, null, requestBody, Charset.forName(charset));
    }

    /**
     * 双向Https Post请求
     *
     * @param requestUrl
     * @param headerMap
     * @param requestBody
     * @param charset
     * @return
     * @throws Exception
     */
    public static String httpsPostRequest(String requestUrl, Map<String, String> headerMap, String requestBody, Charset charset) throws Exception {
        StringBuilder resultMsg;
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(initConfig()).build()) {
            HttpPost httpPost = getHttpPost(requestUrl, headerMap);
            StringEntity stringEntity = new StringEntity(requestBody, charset);
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            resultMsg = send(response);
        }
        return resultMsg.toString();
    }

    /**
     * 单向Http Get请求
     *
     * @param requestUrl
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String singletonHttpsGetRequest(String requestUrl, Map<String, String> paramMap) throws Exception {
        return singletonHttpsGetRequest(requestUrl, null, paramMap);
    }

    /**
     * 单向Https Get请求
     *
     * @param requestUrl
     * @param headerMap
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String singletonHttpsGetRequest(String requestUrl, Map<String, String> headerMap, Map<String, String> paramMap) throws Exception {
        StringBuilder resultSb;
        try (CloseableHttpClient httpClient = getSingletonHttpsClient()) {
            HttpGet httpGet = getHttpGet(requestUrl, headerMap, paramMap);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            resultSb = send(response);
        }
        return resultSb.toString();
    }

    /**
     * 单向Https Post请求
     *
     * @param requestUrl
     * @param headerMap
     * @param param
     * @return
     * @throws Exception
     */
    public static String singletonHttpsPostRequest(String requestUrl, Map<String, String> headerMap, String param) throws Exception {
        StringBuilder resultSb;
        try (CloseableHttpClient httpClient = getSingletonHttpsClient()) {
            HttpPost httpPost = getHttpPost(requestUrl, headerMap);

            StringEntity stringEntity = new StringEntity(param, StandardCharsets.UTF_8);
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            resultSb = send(response);
        }
        return resultSb == null ? null : resultSb.toString();
    }

    public static String singletonHttpsPost(String requestUrl, Map<String, ? extends Object> params, String charSet) throws Exception {
        return singletonHttpsPost(requestUrl, params, Charset.forName(charSet), 1000);
    }

    /**
     * 单向Https Post请求
     *
     * @param requestUrl
     * @param params
     * @param charSet
     * @param timeout
     * @return
     * @throws Exception
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

            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout).setSocketTimeout(timeout).build();
            httpPost.setConfig(requestConfig);

            httpPost.setEntity(new UrlEncodedFormEntity(paris, charSet));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            resultSb = send(response);
        }
        return resultSb.toString();
    }

    /**
     * 获取Http Get请求体
     *
     * @param requestUrl
     * @param headerMap
     * @param paramMap
     * @return
     */
    private static HttpGet getHttpGet(String requestUrl, Map<String, String> headerMap, Map<String, String> paramMap) {
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
     *
     * @param requestUrl
     * @param headerMap
     * @return
     */
    private static HttpPost getHttpPost(String requestUrl, Map<String, String> headerMap) {
        HttpPost httpPost = new HttpPost(requestUrl);
        if (!Objects.isNull(headerMap)) {
            for (String key : headerMap.keySet()) {
                httpPost.addHeader(key, headerMap.get(key));
            }
        }
        return httpPost;
    }


    /**
     * 发送请求
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static StringBuilder send(CloseableHttpResponse response) throws IOException {
        try {
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
        } finally {
            response.close();
        }
    }

    /**
     * 双向https
     *
     * @return
     */
    private static SSLConnectionSocketFactory initConfig() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("jks");
        try (InputStream in = new FileInputStream(TRUST_STORE_FILE)) {
            keyStore.load(in, TRUST_STORE_PASSWORD.toCharArray());
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                    .loadKeyMaterial(keyStore, TRUST_STORE_PASSWORD.toCharArray())
                    .build();
            HostnameVerifier verifier = (s, sslSession) -> true;
            return new SSLConnectionSocketFactory(
                    sslcontext,
                    HTTPS_PROTOCOL,
                    null,
                    verifier
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("初始化client keyStore 失败：" + e.getMessage());
        }
    }

    /**
     * http && 单向https
     *
     * @return
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
        return HttpClientBuilder.create().setConnectionManager(connManager).build();
    }
}