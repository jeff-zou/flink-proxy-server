package io.github.jeff_zou.proxy.netty;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.jeff_zou.proxy.common.BaseResponse;
import io.github.jeff_zou.proxy.kafka.KafkaProducerClient;
import io.github.jeff_zou.proxy.netty.exception.IllegalMethodNotAllowedException;
import io.github.jeff_zou.proxy.netty.exception.IllegalPathNotFoundException;
import io.github.jeff_zou.proxy.proxy.FlinkProxyHandler;
import io.github.jeff_zou.proxy.proxy.IFunctionHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private HashMap<Path, IFunctionHandler> functionHandlerMap = new HashMap<>();

    public HttpServerHandler(
            KafkaProducerClient producerClient,
            Cache<String, ChannelHandlerContext> callbackCache) {
        functionHandlerMap.put(
                new Path("POST", "/prxy", true),
                new FlinkProxyHandler(producerClient, callbackCache));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        FullHttpRequest copyRequest = request.copy();
        onReceivedRequest(ctx, new NettyHttpRequest(copyRequest));
    }

    private void onReceivedRequest(ChannelHandlerContext context, NettyHttpRequest request) {
        FullHttpResponse response = handleHttpRequest(context, request);
        if (response != null) {
            context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            ReferenceCountUtil.release(request);
        }
    }

    private FullHttpResponse handleHttpRequest(
            ChannelHandlerContext context, NettyHttpRequest request) {
        IFunctionHandler functionHandler = null;
        try {
            functionHandler = matchFunctionHandler(request);
            BaseResponse response = functionHandler.execute(context, request);
            if (response != null) {
                return NettyHttpResponse.ok(response);
            }
        } catch (IllegalMethodNotAllowedException error) {
            return NettyHttpResponse.make(HttpResponseStatus.METHOD_NOT_ALLOWED);
        } catch (IllegalPathNotFoundException error) {
            return NettyHttpResponse.make(HttpResponseStatus.NOT_FOUND);
        } catch (Exception error) {
            log.error(functionHandler.getClass().getSimpleName() + " Error", error);
            return NettyHttpResponse.makeError(error);
        }
        return null;
    }

    private IFunctionHandler matchFunctionHandler(NettyHttpRequest request)
            throws IllegalPathNotFoundException, IllegalMethodNotAllowedException {

        AtomicBoolean matched = new AtomicBoolean(false);

        Stream<Path> stream =
                functionHandlerMap.keySet().stream()
                        .filter(
                                ((Predicate<Path>)
                                                path -> {
                                                    if (request.matched(
                                                            path.getUri(), path.isEqual())) {
                                                        matched.set(true);
                                                        return matched.get();
                                                    }
                                                    return false;
                                                })
                                        .and(
                                                path -> {
                                                    return request.isAllowed(path.getMethod());
                                                }));

        Optional<Path> optional = stream.findFirst();

        stream.close();

        if (!optional.isPresent() && !matched.get()) {
            throw new IllegalPathNotFoundException();
        }

        if (!optional.isPresent() && matched.get()) {
            throw new IllegalMethodNotAllowedException();
        }

        return functionHandlerMap.get(optional.get());
    }
}
