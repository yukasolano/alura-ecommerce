package br.com.alura.consumer;

import br.com.alura.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public interface ConsumerService<T> {

    void parse(ConsumerRecord<String, Message<T>> record) throws IOException, ExecutionException, InterruptedException, SQLException;
    String getTopic();
    String getConsumerGroup();

}
