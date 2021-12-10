package com.el.base.utils.support.utils;

import java.io.*;
import java.util.Objects;

/**
 * <br/>
 * since 2021/1/26
 *
 * @author eddie.lys
 */
public class InputStreamUtils {


    public static ByteArrayOutputStream cloneInputStream(InputStream input) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * 转换OutputStream为InputStream
     * @param source           原始对象
     * @return                 InputStream
     */
    public static ByteArrayInputStream convertOutputStreamToInputStream(final OutputStream source) {
        if (Objects.isNull(source)) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) source;
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    /**
     * input stream 转换为byte[]
     * @param input             数据源
     * @return                  数组
     * @throws IOException      io异常
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        if (Objects.isNull(input)) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
