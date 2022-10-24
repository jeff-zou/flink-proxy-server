package io.github.jeff_zou.proxy.proxy;

import io.github.jeff_zou.proxy.common.BaseResponse;
import io.github.jeff_zou.proxy.netty.NettyHttpRequest;
import io.netty.channel.ChannelHandlerContext;

public interface IFunctionHandler<T> {
    BaseResponse execute(ChannelHandlerContext context, NettyHttpRequest request) throws Exception;
}
