package com.aloa.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class CalculationThreadPoolConfig {

    @Bean(name = "calculationExecutor")
    public Executor calculationExecutor() {
        final int CORE_POOL_SIZE = 10;
        final int MAX_POOL_SIZE = 50;
        final int QUEUE_CAPACITY = 100;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("calculation-");
        executor.initialize();
        return executor;
    }
}
