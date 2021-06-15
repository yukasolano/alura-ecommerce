package br.com.alura;

import br.com.alura.consumer.ConsumerService;
import br.com.alura.consumer.ServiceRunner;
import br.com.alura.dispacher.KafkaDispacher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

    private final LocalDatabase localDatabase;

    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(2);
    }

    private KafkaDispacher<Order> kafkaDispacher = new KafkaDispacher<>();

    public FraudDetectorService() throws SQLException {
        this.localDatabase = new LocalDatabase("frauds_database");
        this.localDatabase.createTableIfNotExist("create table Orders (" +
                "uuid varchar(200) primary key," +
                "is_fraud boolean)");
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
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

        Message<Order> message = record.value();
        Order order = message.getPayload();

        if(wasProcessed(order)) {
            System.out.println("Order approved " + order);
            return;
        }

        if (order.getValue().compareTo(new BigDecimal("4000")) >= 0) {
            localDatabase.update("insert into Orders (uuid, is_fraud) values (?, false)", order.getOrderId());
            System.out.println("Order is a fraud " + order);
            kafkaDispacher.send("ECOMMERCE_ORDER_FRAUD", order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        } else {
            localDatabase.update("insert into Orders (uuid, is_fraud) values (?, true)", order.getOrderId());
            System.out.println("Order approved " + order);
            kafkaDispacher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        }
    }

    private boolean wasProcessed(Order order) throws SQLException {
        ResultSet results = localDatabase.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }
}
