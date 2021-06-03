package br.com.alura;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        CreateUserService createUserService = new CreateUserService();
        try (KafkaService<Order> service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", createUserService::parse, new HashMap<>())) {
            service.run();
        }
    }

    CreateUserService() throws SQLException {
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

    private KafkaDispacher<Order> kafkaDispacher = new KafkaDispacher<>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("-------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());

        Order order = record.value().getPayload();
        if (isNewUser(order.getEmail())) {
            inserNewUser(order.getEmail());
        }

    }

    private boolean isNewUser(String email) throws SQLException {
        PreparedStatement exists = connection.prepareStatement("select uuid from Users " +
                "where email = ? limit 1");
        exists.setString(1, email);
        ResultSet resultSet = exists.executeQuery();
        return !resultSet.next();
    }

    private void inserNewUser(String email) throws SQLException {
        PreparedStatement insert = connection.prepareStatement("insert into Users (uuid, email) values (?,?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("Usuário com email: " + email + " adicionado");
    }
}
