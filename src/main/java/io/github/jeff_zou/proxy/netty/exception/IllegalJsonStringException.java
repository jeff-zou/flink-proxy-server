package io.github.jeff_zou.proxy.netty.exception;

public class IllegalJsonStringException extends Exception {
    public IllegalJsonStringException() {
        super("PARSE JSON STRING ERROR");
    }
}
