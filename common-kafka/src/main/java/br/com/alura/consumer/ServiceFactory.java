package br.com.alura.consumer;

import java.sql.SQLException;

public interface ServiceFactory<T> {

    ConsumerService<T> create() throws SQLException;
 }
