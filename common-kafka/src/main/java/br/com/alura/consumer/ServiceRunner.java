package br.com.alura.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceRunner<T> {
    private final ServicerProvider<T> provider;

    public ServiceRunner(ServiceFactory<T> factory) {
        this.provider = new ServicerProvider<>(factory);
    }

    public void start(int threadCount) {
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            pool.submit(provider);
        }

    }
}
