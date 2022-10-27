package io.github.jeff_zou.proxy.udf;

import org.apache.flink.table.functions.ScalarFunction;

/**
 * @Author: Jeff Zou @Date: 2022/10/25 17:43
 */
public class IpCity extends ScalarFunction {
    public String eval(String ip) {
        if (ip.indexOf(".") < 0 && ip.indexOf(":") < 0) {
            return null;
        }
        // return getCityFromIp(ip);
        return "珠海";
    }

    /*    private String getCityFromIp(String ip){
        return "";
    }*/
}
