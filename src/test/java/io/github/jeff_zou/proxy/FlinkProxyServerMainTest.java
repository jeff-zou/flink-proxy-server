package io.github.jeff_zou.proxy;

import io.github.jeff_zou.proxy.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * @Author: Jeff Zou @Date: 2022/10/20 13:41
 */
class FlinkProxyServerMainTest {

    @Test
    void testProperties() throws Exception {
        Properties properties = PropertiesUtil.load("comsumer.properties");
        System.out.println(properties.getProperty("topic"));
    }
}
