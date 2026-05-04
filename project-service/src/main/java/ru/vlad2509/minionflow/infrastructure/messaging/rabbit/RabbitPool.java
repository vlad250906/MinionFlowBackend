package ru.vlad2509.minionflow.infrastructure.messaging.rabbit;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ApplicationScoped
public class RabbitPool {

    private final ExecutorService executor;

    public RabbitPool(@ConfigProperty(name = "service-common.rabbit-pool-size", defaultValue = "4") int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public Future<Void> execute(Runnable runnable){
        return CompletableFuture.runAsync(runnable, executor);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

}
