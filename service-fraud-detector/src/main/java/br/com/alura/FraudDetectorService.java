package br.com.alura;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    public static void main(String[] args) {
        FraudDetectorService fraudService = new FraudDetectorService();
        try (KafkaService<Order> service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", fraudService::parse, Order.class, new HashMap<>())) {
            service.run();
        }
    }

    private KafkaDispacher<Order> kafkaDispacher = new KafkaDispacher<>();

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("-------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Order order = record.value();
        if (order.getValue().compareTo(new BigDecimal("4000")) >= 0) {
            System.out.println("Order is a fraud " + order);
            kafkaDispacher.send("ECOMMERCE_ORDER_FRAUD", order.getEmail(), order);
        } else {
            System.out.println("Order approved " + order);
            kafkaDispacher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), order);
        }
    }
}
