package io.github.jeff_zou.proxy;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.jeff_zou.proxy.kafka.KafkaConsumerClient;
import io.github.jeff_zou.proxy.kafka.KafkaProducerClient;
import io.github.jeff_zou.proxy.netty.NettyHttpServer;
import io.github.jeff_zou.proxy.netty.NettyHttpServerConfig;
import io.github.jeff_zou.proxy.proxy.CallbackCacheConfig;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: Jeff Zou @Date: 2022/10/18 10:52
 */
public class FlinkProxyServerMain {
    public static void main(String[] args) throws Exception {
        // 创建caffeine缓存，用于缓存http请求的context,等消费者收到flink结果后,从缓存中拿出请求的context并写入结果
        CallbackCacheConfig cacheConfig = new CallbackCacheConfig.Builder().build();
        Cache<String, ChannelHandlerContext> callbackCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(cacheConfig.getCacheExpire())
                        .maximumSize(cacheConfig.getCacheMaxSize())
                        .build();

        KafkaConsumerClient consumerClient = new KafkaConsumerClient();
        // 启动消费者
        consumerClient.createConsumerClient(callbackCache);

        // 创建生产者，用于将收到的http请求转发至kafka，为flink提供数据
        KafkaProducerClient producerClient = new KafkaProducerClient();

        // 启动http服务
        NettyHttpServerConfig serverConfig = new NettyHttpServerConfig.Builder().build();
        NettyHttpServer nettyHttpServer =
                new NettyHttpServer(serverConfig, producerClient, callbackCache);
        nettyHttpServer.startServer();
    }
}
