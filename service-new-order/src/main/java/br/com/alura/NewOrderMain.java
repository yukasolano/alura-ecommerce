package br.com.alura;

import br.com.alura.dispacher.KafkaDispacher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (KafkaDispacher<Order> orderDispacher = new KafkaDispacher<>()) {
            for (int i = 0; i < 10; i++) {
                String orderId = UUID.randomUUID().toString();
                BigDecimal value = BigDecimal.valueOf(Math.random() * 5000 + 1);
                String email = Math.random() + "@email.com";
                Order order = new Order(orderId, value, email);

                //mesmas compras do mesmo usuario sao processadas sequenciamente
                orderDispacher.send("ECOMMERCE_NEW_ORDER", email,
                        new CorrelationId(NewOrderMain.class.getSimpleName()), order);
            }
        }
    }
}


