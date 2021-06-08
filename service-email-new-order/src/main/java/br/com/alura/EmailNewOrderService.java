package br.com.alura;

import br.com.alura.consumer.KafkaService;
import br.com.alura.dispacher.KafkaDispacher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EmailNewOrderService emailService = new EmailNewOrderService();
        try (KafkaService<Order> service = new KafkaService<>(EmailNewOrderService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", emailService::parse, new HashMap<>())) {
            service.run();
        }
    }

    private KafkaDispacher<Email> emailDispacher = new KafkaDispacher<>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("-------------------------");
        System.out.println("Processing new order, preparing email");
        Message<Order> message = record.value();
        System.out.println(message);

        Order order = message.getPayload();
        Email emailCode = new Email("New order", "Thank you for your order! We are processing your order!");
        CorrelationId id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispacher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), id, emailCode);

    }
}

