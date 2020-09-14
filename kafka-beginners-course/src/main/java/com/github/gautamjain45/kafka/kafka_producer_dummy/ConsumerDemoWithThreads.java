package com.github.gautamjain45.kafka.kafka_producer_dummy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoWithThreads {

    public static void main(String[] args) {
        new ConsumerDemoWithThreads().run();
    }

    private ConsumerDemoWithThreads(){
    }

    private void run(){
        //Creating logger
        Logger log = LoggerFactory.getLogger(ConsumerDemoWithThreads.class.getName());

        //Consumer Property Variables
        String bootStrapServer = "127.0.0.1:9092";
        String groupId = "my_sixth_application";
        String topic = "first_topic";

        //Latch for dealing with multiple threads.
        CountDownLatch latch = new CountDownLatch(1);

        //Creating consumer runnable
        log.info("Creating Consumer thread");
        ConsumerRunnable myConsumerRunnable = new ConsumerRunnable(bootStrapServer,groupId,topic,latch);

        //Start the thread
        Thread myThread = new Thread(myConsumerRunnable);
        myThread.start();

        //Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            log.info("Caught shutdown hook");
            ((ConsumerRunnable)myConsumerRunnable).Shutdown();

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            log.info("Application has excited.");
        }

        ));

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Application got interrupted",e);
        }
        finally {
            log.info("Application is closing!");
        }
    }


    public class ConsumerRunnable implements Runnable{

        private CountDownLatch latch;
        private KafkaConsumer<String,String> consumer;

        //creating logger
        private Logger log = LoggerFactory.getLogger(ConsumerRunnable.class.getName());

        public ConsumerRunnable(String bootStrapServer,String groupId,String topic, CountDownLatch latch){
            this.latch = latch;

            //Setting Consumer properties
            Properties prop = new Properties();
            prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootStrapServer);
            prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
            prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

            //Create kafka consumer
            consumer = new KafkaConsumer<String, String>(prop);

            //Subscribe consumer to a topic
            consumer.subscribe(Collections.singleton(topic));

            //To subscribe to multiple topic
            //consumer.subscribe(Arrays.asList("first_topic","second_topic"));
        }

        @Override
        public void run() {
            try {
                //Poll new data
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> record : records) {
                        log.info("Key: " + record.key() + " , Value: " + record.value());
                        log.info("Partition: " + record.partition() + " , OffSet: " + record.offset());
                    }

                }
            }
            catch (WakeupException e){
                log.info("Received Shutdown Signal!");
            }
            finally {
                consumer.close();
                //tell the main code that we are done with consumer
                latch.countDown();
            }
        }

        public void Shutdown(){

            //Wakeup is a special method which is used to interrupt consumer.poll method
            //It will throw exception WakeUpException
            consumer.wakeup();
        }
    }

}
