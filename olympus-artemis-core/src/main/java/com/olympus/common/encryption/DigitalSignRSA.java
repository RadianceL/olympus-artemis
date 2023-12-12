package com.olympus.common.encryption;

import java.security.*;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 公钥加密 私钥解密
 * since 9/13/22
 *
 * @author eddie
 */
public class DigitalSignRSA {

    /**
     * 编码规则
     */
    public static final String CHARSET = "UTF-8";
    /**
     * 算法名称
     */
    public static final String RSA_ALGORITHM = "RSA";
    /**
     * RSA公钥长度
     */
    private static final Integer DEFAULT_KEY_SIZE = 512;

    public static Map<String, String> createKeys() {
        //为RSA算法创建一个KeyPairGenerator对象（KeyPairGenerator，密钥对生成器，用于生成公钥和私钥对）
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }
        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(DEFAULT_KEY_SIZE);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded()); //返回一个publicKey经过二次加密后的字符串
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        return keyPairMap;
    }

    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 得到私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }


    /**
     * 私钥解密
     */
    public static String privateDecrypt(String data, String privateKey) {
        try {
            RSAPrivateKey reaPrivateKey = DigitalSignRSA.getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, reaPrivateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), reaPrivateKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     */
    public static String privateEncrypt(String data, String privateKey) {
        try {
            RSAPrivateKey reaPrivateKey = DigitalSignRSA.getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, reaPrivateKey);
            return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), reaPrivateKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥加密
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     */
    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), publicKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) throws IOException {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int offSet = 0;
            byte[] buff;
            int i = 0;
            try {
                while (datas.length > offSet) {
                    if (datas.length - offSet > maxBlock) {
                        buff = cipher.doFinal(datas, offSet, maxBlock);
                    } else {
                        buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                    }
                    out.write(buff, 0, buff.length);
                    i++;
                    offSet = i * maxBlock;
                }
            } catch (Exception e) {
                throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
            }
            return out.toByteArray();
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> keyMap = DigitalSignRSA.createKeys();
//        String  publicKey = keyMap.get("publicKey");
//        String  privateKey = keyMap.get("privateKey");
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIxG3uzxLl83ZHlWeHRPGnt9R28MJzO5XygQAMdphY2FTEvVTZXiMCuGSeBt9tx7Z7P/G8N8adlAs+VwYYtHY7UCAwEAAQ==";
        String privateKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAjEbe7PEuXzdkeVZ4dE8ae31HbwwnM7lfKBAAx2mFjYVMS9VNleIwK4ZJ4G323Htns/8bw3xp2UCz5XBhi0djtQIDAQABAkAFtr9O3HYcl8eGb8DBUBWOkShPPCn+n97h5WD6vnAbtxJR8dgd0Wl/yOiGxrdw4XU+7HLukn/iLr2wBpphX6OjAiEAs2Wetj3L5XmAnycnyi9QjdiO/9Hcicseh1slINNNEjMCIQDILOwe1SKuH3Nkde4qzA5ns6tjVoQRqj5hZwYF+7padwIgONoOdxCUak+cDFPbTz65V4p2nunB60Uckqa22HU+KnMCIQCxOMRbj8tCaWnaGWag0UQ3n6QvMJuAoD7WMUXt6i3UhQIgMy++14f81k01VouEOQoMW34+QoRto4puxT829antCGY=";
        System.out.println("公钥: \n\r" + publicKey);
        System.out.println("私钥： \n\r" + privateKey);


        System.out.println("公钥加密——私钥解密");
        String str =
                "{" +
                        "    \"pageIndex\": 1,\n" +
                        "    \"pageSize\": 10,\n" +
                        "    \"type\": \"1\",\n" +
                        "    \"settlementPeriodFrom\": \"2023-01-01\",\n" +
                        "    \"settlementPeriodTo\": \"2023-01-31\",\n" +
                        "    \"startTime\": \"2023-01-01 00:00:00\",\n" +
                        "    \"endTime\": \"2023-01-31 00:00:00\"\n" +
                        "}";
        System.out.println("\r明文：\r\n" + str);
        System.out.println("\r明文大小：\r\n" + str.getBytes().length);
        String encodedData = DigitalSignRSA.publicEncrypt(str, DigitalSignRSA.getPublicKey(publicKey));
        System.out.println("密文：\r\n" + encodedData);
        String decodedData = DigitalSignRSA.privateDecrypt(encodedData, privateKey);
        System.out.println("解密后文字: \r\n" + decodedData);
    }
}
