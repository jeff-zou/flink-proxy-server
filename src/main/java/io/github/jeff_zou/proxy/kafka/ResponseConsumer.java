package io.github.jeff_zou.proxy.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.jeff_zou.proxy.common.BaseResponse;
import io.github.jeff_zou.proxy.netty.NettyHttpResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class ResponseConsumer implements Runnable {

    private Properties properties;

    private String topic;

    private ObjectMapper objectMapper;

    private Cache<String, ChannelHandlerContext> callbackCache;

    public ResponseConsumer(
            String topic,
            Properties properties,
            Cache<String, ChannelHandlerContext> callbackCache) {
        this.properties = properties;
        this.topic = topic;
        this.objectMapper = new ObjectMapper();
        this.callbackCache = callbackCache;
    }

    @Override
    public void run() {
        KafkaConsumer consumer = new KafkaConsumer(properties);
        log.info(
                "threadId:{} init consumer for : {} groupId:{} topic:{}",
                Thread.currentThread().getId(),
                properties.getProperty("bootstrap.servers"),
                properties.getProperty("group.id"),
                topic);
        consumer.subscribe(Arrays.asList(topic));
        consumer(consumer);
    }

    public void consumer(KafkaConsumer consumer) {
        log.info("start consumer {}.....", Thread.currentThread().getId());

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(20));
                if (records.count() == 0) {
                    continue;
                }

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(record.value());
                        String cacheKey =
                                new StringBuilder(jsonNode.get("app_id").textValue())
                                        .append("_")
                                        .append(jsonNode.get("proxy_type").textValue())
                                        .append("_")
                                        .append(jsonNode.get("uuid").longValue())
                                        .toString();

                        log.info(
                                "receive result time : {} cacheKey={} offset = {}, partition={}, key = {}, value = {} ",
                                System.currentTimeMillis(),
                                cacheKey,
                                record.offset(),
                                record.partition(),
                                record.key(),
                                record.value());

                        ChannelHandlerContext context = callbackCache.getIfPresent(cacheKey);
                        if (context == null) {
                            continue;
                        }

                        BaseResponse response =
                                BaseResponse.builder()
                                        .message(jsonNode.get("proxy_result"))
                                        .build();
                        context.writeAndFlush(
                                NettyHttpResponse.ok(objectMapper.writeValueAsString(response)));

                    } catch (Exception e) {
                        log.error("error cache for: {}", record.value(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                consumer.close();
                log.info("close consumer {}.....", Thread.currentThread().getId());
            } catch (Exception e1) {
                log.error("", e1);
            }
        }
    }
}
