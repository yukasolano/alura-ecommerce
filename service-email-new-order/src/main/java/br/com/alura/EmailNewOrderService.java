package br.com.alura;

import br.com.alura.consumer.ConsumerService;
import br.com.alura.consumer.ServiceRunner;
import br.com.alura.dispacher.KafkaDispacher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    public static void main(String[] args) {
        new ServiceRunner<>(EmailNewOrderService::new).start(1);
    }

    private KafkaDispacher<Email> emailDispacher = new KafkaDispacher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("-------------------------");
        System.out.println("Processing new order, preparing email");
        Message<Order> message = record.value();
        System.out.println(message);

        Order order = message.getPayload();
        Email emailCode = new Email("New order", "Thank you for your order! We are processing your order!");
        CorrelationId id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispacher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), id, emailCode);

    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}

