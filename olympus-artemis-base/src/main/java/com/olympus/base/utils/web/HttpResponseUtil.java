package com.olympus.base.utils.web;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * HttpResponse返回消息辅助工具
 * since 2019/12/1
 *
 * @author eddie
 */
public class HttpResponseUtil {

    @SneakyThrows
    public static void send(HttpServletResponse httpServletResponse, Object response){
        if (Objects.isNull(response)) {
            return;
        }

        httpServletResponse.setContentType("application/json;charset=utf-8");

        try (PrintWriter printer = httpServletResponse.getWriter()) {
            String resp = JSON.toJSONString(response);
            printer.write(resp);
            printer.flush();
        }
    }

    /**
     * 下载文件
     *
     * @param response  HTTP响应对象
     * @param file      输出对象
     * @throws IOException 抛出异常，由调用者捕获处理
     */
    public static void write(HttpServletResponse response, File file) throws IOException {
        String fileName = file.getName();
        try (
                FileInputStream in = new FileInputStream(file)
        ) {
            // 对文件名进行URL转义，防止中文乱码
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

            // 空格用URLEncoder.encode转义后会变成"+"，所以要替换成"%20"，浏览器会解码回空格
            fileName = fileName.replace("+", "%20");

            // "+"用URLEncoder.encode转义后会变成"%2B"，所以要替换成"+"，浏览器不对"+"进行解码
            fileName = fileName.replace("%2B", "+");
            write(response, fileName, in);
        }
    }


    /**
     * 下载文件
     *
     * @param response  HTTP响应对象
     * @param file      输出对象
     * @throws IOException 抛出异常，由调用者捕获处理
     */
    public static void write(HttpServletResponse response, String fileName, InputStream file) throws IOException {
        try (
                OutputStream out = response.getOutputStream()
        ) {
            response.setContentType("application/x-msdownload;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            byte[] bytes = new byte[4096];
            int len;
            while ((len = file.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            out.flush();
        }
    }
}
