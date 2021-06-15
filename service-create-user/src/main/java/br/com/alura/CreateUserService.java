package br.com.alura;

import br.com.alura.consumer.ConsumerService;
import br.com.alura.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {


    private final LocalDatabase localDatabase;

    public static void main(String[] args) {
        new ServiceRunner<>(CreateUserService::new).start(1);
    }

    public CreateUserService() throws SQLException {
        this.localDatabase = new LocalDatabase("users_database");
        this.localDatabase.createTableIfNotExist("create table Users (" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("-------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());

        Order order = record.value().getPayload();
        if (isNewUser(order.getEmail())) {
            inserNewUser(order.getEmail());
        }

    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    private boolean isNewUser(String email) throws SQLException {
        ResultSet resultSet = localDatabase.query("select uuid from Users " +
                "where email = ? limit 1", email);
        return !resultSet.next();
    }

    private void inserNewUser(String email) throws SQLException {
        localDatabase.update("insert into Users (uuid, email) values (?,?)", UUID.randomUUID().toString(), email);
        System.out.println("Usuário com email: " + email + " adicionado");
    }
}
