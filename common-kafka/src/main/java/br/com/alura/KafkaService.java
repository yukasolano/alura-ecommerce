package br.com.alura;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction<T> parse;

    public KafkaService(String groupId,
                        String topic,
                        ConsumerFunction<T> parse,
                        Map<String, String> properties) {

        this.consumer = new KafkaConsumer<>(getProperties(groupId, properties));
        this.parse = parse;
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId,
                        Pattern topic,
                        ConsumerFunction<T> parse,
                        Map<String, String> properties) {
        this.consumer = new KafkaConsumer<>(getProperties(groupId, properties));
        this.parse = parse;
        consumer.subscribe(topic);
    }

    public void run() throws ExecutionException, InterruptedException {
        try(KafkaDispacher deadLetter = new KafkaDispacher()) {
            while (true) {
                ConsumerRecords<String, Message<T>> records = consumer.poll(Duration.ofMillis(100));
                if (!records.isEmpty()) {
                    System.out.println("Encontrei " + records.count() + " registros");

                    for (ConsumerRecord<String, Message<T>> record : records) {
                        try {
                            parse.consume(record);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Message<T> message = record.value();
                            deadLetter.send("ECOMMERCE_DEADLETTER", message.getId().toString(),
                                    message.getId().continueWith("DeadLetter"),
                                    new GsonSerializer<>().serialize("", message));
                        }
                    }
                }
            }
        }
    }

    private Properties getProperties(String groupId,
                                     Map<String, String> overrideProperties) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
