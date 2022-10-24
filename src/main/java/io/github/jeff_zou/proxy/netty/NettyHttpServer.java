package io.github.jeff_zou.proxy.netty;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.jeff_zou.proxy.kafka.KafkaProducerClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpServer {
    private NettyHttpServerConfig serverConfig;
    private KafkaProducerClient producerClient;
    private Cache<String, ChannelHandlerContext> callbackCache;

    public NettyHttpServer(
            NettyHttpServerConfig serverConfig,
            KafkaProducerClient producerClient,
            Cache<String, ChannelHandlerContext> callbackCache) {
        this.serverConfig = serverConfig;
        this.producerClient = producerClient;
        this.callbackCache = callbackCache;
    }

    public void startServer() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(serverConfig.getBossThreadNum());
        EventLoopGroup workerGroup = new NioEventLoopGroup(serverConfig.getWorkerThreadNum());

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(NioChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(NioChannelOption.SO_KEEPALIVE, false);
        bootstrap.childOption(NioChannelOption.SO_RCVBUF, 2048);
        bootstrap.childOption(NioChannelOption.SO_SNDBUF, 2048);
        bootstrap.childHandler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("codec", new HttpServerCodec());
                        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(64 * 1024));
                        ch.pipeline().addLast("interceptor", new InterceptorHandler());
                        ch.pipeline()
                                .addLast(
                                        "bizHandler",
                                        new HttpServerHandler(producerClient, callbackCache));
                    }
                });
        ChannelFuture channelFuture =
                bootstrap
                        .bind(serverConfig.getPort())
                        .syncUninterruptibly()
                        .addListener(
                                future -> {
                                    log.info("start http server,port:{}", serverConfig.getPort());
                                });
        channelFuture
                .channel()
                .closeFuture()
                .addListener(
                        future -> {
                            log.info("Netty Http Server Start Shutdown ............");
                            bossGroup.shutdownGracefully();
                            workerGroup.shutdownGracefully();
                        });
    }
}
