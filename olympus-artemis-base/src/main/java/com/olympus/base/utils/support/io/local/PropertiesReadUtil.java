package com.olympus.base.utils.support.io.local;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 配置读取工具
 * since 2019/12/5
 *
 * @author eddie
 */
@Slf4j
public class PropertiesReadUtil {
    public static Map<String, String> getProperties(String name) {
        URL resource = PropertiesReadUtil.class.getClassLoader().getResource(name);
        if (Objects.isNull(resource)) {
            throw new RuntimeException("file not exist");
        }
        Map<String, String> propertiesMap = new HashMap<>(16);
        String filePath = resource.getPath();
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] propertiesInfo = line.split("=");
                propertiesMap.put(propertiesInfo[0], propertiesInfo[1]);
            }

            return propertiesMap;
        } catch (IOException e) {
            throw new RuntimeException("Parsing the configuration file failed. ");
        }
    }
}
