package br.com.alura.consumer;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ServicerProvider<T> implements Callable<Void> {

    private final ServiceFactory<T> factory;

    public ServicerProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    public Void call() throws ExecutionException, InterruptedException {

        ConsumerService<T> myService = factory.create();

        try (KafkaService<T> service = new KafkaService<>(myService.getConsumerGroup(),
                myService.getTopic(), myService::parse, new HashMap<>())) {
            service.run();
        }
        return null;
    }
}
