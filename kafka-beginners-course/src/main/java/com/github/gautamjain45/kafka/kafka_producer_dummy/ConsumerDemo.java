package com.github.gautamjain45.kafka.kafka_producer_dummy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {

    public static void main(String[] args) {
        //Creating logger
        Logger log = LoggerFactory.getLogger(ConsumerDemo.class.getName());

        //Consumer Property Variables
        String bootStrapServer = "127.0.0.1:9092";
        String groupId = "my_fourth_application";
        String topic = "first_topic";

        //Setting Consumer properties
        Properties prop = new Properties();
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootStrapServer);
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        //Create kafka consumer
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(prop);

        //Subscribe consumer to a topic
        consumer.subscribe(Collections.singleton(topic));

        //To subscribe to multiple topic
        //consumer.subscribe(Arrays.asList("first_topic","second_topic"));

        //Poll new data
        while (true) {
         ConsumerRecords<String,String> records =  consumer.poll(Duration.ofMillis(100));

         for(ConsumerRecord<String,String> record : records){
            log.info("Key: " + record.key() + " , Value: " + record.value());
            log.info("Partition: " + record.partition() + " , OffSet: " + record.offset());
         }

        }
    }
}
