package io.github.jeff_zou.proxy.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author: Jeff Zou @Date: 2022/10/24 15:48
 */
@Slf4j
public class PropertiesUtil {
    public static Properties load(String fileName) throws Exception {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(getCurrentClassLoader().getResourceAsStream(fileName));
            properties.load(in);
        }catch (Exception e){
            log.error("load file error: {}", fileName, e);
            throw e;
        } finally {
            if(in!=null){
                in.close();
            }
        }

        return properties;
    }

    public static ClassLoader getCurrentClassLoader()
    {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader == null)
        {
            classLoader = PropertiesUtil.class.getClassLoader();
        }
        return classLoader;
    }
}
