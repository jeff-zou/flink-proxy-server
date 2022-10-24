package io.github.jeff_zou.proxy.netty.exception;

public class IllegalMethodNotAllowedException extends Exception {
    public IllegalMethodNotAllowedException() {
        super("METHOD NOT ALLOWED");
    }
}
