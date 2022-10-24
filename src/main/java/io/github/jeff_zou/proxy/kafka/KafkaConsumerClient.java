package io.github.jeff_zou.proxy.kafka;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.jeff_zou.proxy.util.PropertiesUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Properties;

@Slf4j
public class KafkaConsumerClient {
    private Properties props;
    private String topic;

    public KafkaConsumerClient() throws Exception {
        this.props = PropertiesUtil.load("comsumer.properties");
        this.topic = props.getProperty("topic");
    }

    public void createConsumerClient(Cache<String, ChannelHandlerContext> callbackCache) {
        KafkaConsumer partitionConsumer = new KafkaConsumer(props);
        List<PartitionInfo> list = partitionConsumer.partitionsFor(topic);
        int size = list.size();
        partitionConsumer.close();

        for (int i = 0; i < size; i++) {
            ResponseConsumer responseConsumer = new ResponseConsumer(topic, props, callbackCache);
            Thread thread = new Thread(responseConsumer);
            thread.start();
            log.info("start consumer:{} ", i);
        }
    }
}
