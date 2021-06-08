package br.com.alura;

import br.com.alura.consumer.KafkaService;
import br.com.alura.dispacher.KafkaDispacher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

    private final Connection connection;

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        BatchSendMessageService batchService = new BatchSendMessageService();
        try (KafkaService<String> service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS", batchService::parse, new HashMap<>())) {
            service.run();
        }
    }

    BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:service-create-user/target/users_database.db";
        connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users (" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200))");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private KafkaDispacher<User> userDispacher = new KafkaDispacher<>();

    private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException, ExecutionException, InterruptedException {
        System.out.println("-------------------------");
        System.out.println("Processing new batch");
        Message<String> message = record.value();
        System.out.println("Topic: " + message.getPayload());

        for (User user : getAllUsers()) {
            userDispacher.send(message.getPayload(), user.getUuid(),
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()), user);
        }

    }

    private List<User> getAllUsers() throws SQLException {
        ResultSet results = connection.prepareStatement("select uuid from Users ").executeQuery();

        List<User> users = new ArrayList<>();
        while (results.next()) {
            users.add(new User(results.getString(1)));
        }
        return users;
    }

}