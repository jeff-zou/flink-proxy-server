package io.github.jeff_zou.proxy.kafka;

import io.github.jeff_zou.proxy.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @Author: Jeff Zou @Date: 2021/11/12.14:01
 */
@Slf4j
public class KafkaProducerClient {

    private Producer<String, String> producer;
    private String topic;

    public KafkaProducerClient() throws Exception {
        Properties properties = PropertiesUtil.load("consumer.properties");
        this.topic = properties.getProperty("topic");

        producer = new org.apache.kafka.clients.producer.KafkaProducer(properties);

        log.info("init producer for : {}", properties.getProperty("bootstrap.servers"));
    }

    public void send(String content) {
        producer.send(new ProducerRecord<String, String>(topic, content));
        producer.flush();
    }
}
