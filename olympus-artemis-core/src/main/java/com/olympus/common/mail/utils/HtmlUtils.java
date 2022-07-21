package com.olympus.common.mail.utils;

import com.olympus.base.utils.collection.CollectionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * <br/>
 *
 * @author eddie.lys
 * @since 2021/3/2
 */
public class HtmlUtils {

    private static final VelocityEngine VE = new VelocityEngine();

    static {
        //初始化参数
        Properties properties = new Properties();
        //设置velocity资源加载方式为class
        properties.setProperty("resource.loader", "class");
        //设置velocity资源加载方式为file时的处理类
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VE.init(properties);
    }

    public static String getHtml(String filePath, Map<String, String> data) {
        Template t = VE.getTemplate(filePath, "UTF-8");
        VelocityContext context = new VelocityContext();
        if (!CollectionUtils.isEmpty(data)) {
            for (String key : data.keySet()) {
                context.put(key, data.get(key));
            }
        }
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public static String getHtmlByStr(String htmlStr, Map<String, String> data) {
        VelocityContext context = new VelocityContext();
        if (!CollectionUtils.isEmpty(data)) {
            for (String key : data.keySet()) {
                context.put(key, data.get(key));
            }
        }
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "getHtmlByStr", htmlStr);
        return writer.toString();
    }
}
