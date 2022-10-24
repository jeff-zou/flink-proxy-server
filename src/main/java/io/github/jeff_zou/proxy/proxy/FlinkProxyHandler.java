package io.github.jeff_zou.proxy.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.jeff_zou.proxy.common.BaseResponse;
import io.github.jeff_zou.proxy.common.ResultCode;
import io.github.jeff_zou.proxy.kafka.KafkaProducerClient;
import io.github.jeff_zou.proxy.netty.NettyHttpRequest;
import io.github.jeff_zou.proxy.netty.NettyHttpResponse;
import io.github.jeff_zou.proxy.netty.exception.IllegalJsonStringException;
import io.github.jeff_zou.proxy.netty.exception.ParamValidException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class FlinkProxyHandler implements IFunctionHandler<BaseResponse> {

    private KafkaProducerClient kafkaProducerClient;
    private Cache<String, ChannelHandlerContext> callbackCache;

    public FlinkProxyHandler(
            KafkaProducerClient kafkaProducerClient,
            Cache<String, ChannelHandlerContext> callbackCache) {
        this.kafkaProducerClient = kafkaProducerClient;
        this.callbackCache = callbackCache;
    }

    @Override
    public BaseResponse execute(ChannelHandlerContext context, NettyHttpRequest request)
            throws Exception {
        // 数据检验，必须为json字符串，并且要包含app_id, proxy_type
        String json = request.contentText();
        JsonNode jsonNode = null;
        try {
            jsonNode = NettyHttpResponse.objectMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalJsonStringException();
        }

        JsonNode appId = jsonNode.get("app_id");
        if (appId == null) {
            throw new ParamValidException("missing param app_id");
        }

        JsonNode proxyType = jsonNode.get("proxy_type");
        if (proxyType == null) {
            throw new ParamValidException("missing param proxy_type");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stime", System.currentTimeMillis());
        map.put("uuid", ThreadLocalRandom.current().nextLong());
        map.put("app_id", appId);
        map.put("proxy_type", proxyType);
        map.put("ext", jsonNode);

        StringBuilder cacheKeyBuilder =
                new StringBuilder(appId.textValue()).append("_").append(proxyType.textValue());
        cacheKeyBuilder.append("_").append(map.get("uuid"));

        return proxy(cacheKeyBuilder.toString(), map, context);
    }

    public BaseResponse proxy(
            String cacheKey, Map<String, Object> map, ChannelHandlerContext context) {
        try {
            String newJson = NettyHttpResponse.objectMapper.writeValueAsString(map);
            callbackCache.put(cacheKey, context);
            kafkaProducerClient.send(newJson);
        } catch (Exception e) {
            log.error("", e);
            return BaseResponse.builder()
                    .code(ResultCode.INTERNAL_SERVER_ERROR)
                    .message(ResultCode.INTERNAL_SERVER_ERROR.getMsg())
                    .build();
        }

        return null;
    }
}
