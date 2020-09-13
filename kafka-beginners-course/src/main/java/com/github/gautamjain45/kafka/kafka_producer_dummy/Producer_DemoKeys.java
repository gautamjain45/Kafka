//This class is having callback functionality which will tell us some details
//regarding records being sent to which partition
//and it will also give us details regarding offset and timestamp.

package com.github.gautamjain45.kafka.kafka_producer_dummy;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Producer_DemoKeys {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //Logger object
        final Logger log = LoggerFactory.getLogger(Producer_DemoKeys.class);

        //Creating kafka properties variables
        String bootstrapServers = "127.0.0.1:9092";

        //Setting Properties For Kafka Producer
        Properties prop = new Properties();
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Create Kafka Producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(prop);

        //Looping and sending messages in bulk and iteratively
        for(int i = 0; i<=10; i++) {

            String Topic = "first_topic";
            String Value = "Message from jave code " + Integer.toString(i);
            String Key = "id_" + Integer.toString(i);

            //Create Producer Record
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(Topic, Key, Value);

            //Log the key
            log.info("Key: " + Key);

            //Send Data - asynchronous
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //executes every time a record is sent successfully or exception is thrown
                    if (e == null) {
                        //when there is no exception
                        log.info("Received new metadata " + "\n" +
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "OffSet: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else {
                        log.error("Exception occurred while producing ", e);
                    }
                }
            }).get();       //block send to make it synchronous -  but highly discouraged in PRD env.
        }

        //Flush Data
        producer.flush();

        //Flush And Close
        producer.close();

    }
}
