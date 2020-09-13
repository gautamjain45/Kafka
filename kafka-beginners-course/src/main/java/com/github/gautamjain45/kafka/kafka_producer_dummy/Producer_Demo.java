package com.github.gautamjain45.kafka.kafka_producer_dummy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Producer_Demo {

    public static void main(String[] args) {
        //Creating kafka properties variables
        String bootstrapServers = "127.0.0.1:9092";

        //Setting Properties For Kafka Producer
        Properties prop = new Properties();
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Create Kafka Producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(prop);

        //Create Producer Record
        ProducerRecord<String,String> record = new ProducerRecord<String, String>("first_topic","Message from java code!!");

        //Send Data - asynchronous
        producer.send(record);

        //Flush Data
        producer.flush();

        //Flush And Close
        producer.close();

    }
}
