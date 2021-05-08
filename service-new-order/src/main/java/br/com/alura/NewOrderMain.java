package br.com.alura;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (KafkaDispacher<Order> orderDispacher = new KafkaDispacher<>()) {
            try (KafkaDispacher<Email> emailDispacher = new KafkaDispacher<>()) {
                for (int i = 0; i < 10; i++) {
                    String userId = UUID.randomUUID().toString();
                    String orderId = UUID.randomUUID().toString();
                    BigDecimal value = BigDecimal.valueOf(Math.random() * 5000 + 1);
                    Order order = new Order(userId, orderId, value);

                    orderDispacher.send("ECOMMERCE_NEW_ORDER", userId, order);

                    Email email = new Email("New order", "Thank you for your order! We are processing your order!");
                    emailDispacher.send("ECOMMERCE_SEND_EMAIL", userId, email);
                }
            }
        }
    }
}


