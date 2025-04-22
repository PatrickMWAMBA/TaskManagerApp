package com.taskmanager.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.taskmanager.app.repository")
@EntityScan(basePackages = "com.taskmanager.app.entity")
public class TaskManagerAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerAppApplication.class, args);
    }
}
