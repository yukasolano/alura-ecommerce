package br.com.alura.consumer;

public interface ServiceFactory<T> {

    ConsumerService<T> create();
 }
