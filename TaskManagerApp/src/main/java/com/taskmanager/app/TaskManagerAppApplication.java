package com.taskmanager.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskManagerAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerAppApplication.class, args);
    }
}
