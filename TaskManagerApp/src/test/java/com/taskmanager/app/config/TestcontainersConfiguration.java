package com.taskmanager.app.config;

import javax.sql.DataSource;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@TestConfiguration
public class TestcontainersConfiguration {

    @Bean
    public MySQLContainer<?> mysqlContainer() {
        MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
        mysql.start();
        return mysql;
    }

    @Bean
    public DataSource dataSource(MySQLContainer<?> mysql) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(mysql.getJdbcUrl());
        hikariConfig.setUsername(mysql.getUsername());
        hikariConfig.setPassword(mysql.getPassword());
        hikariConfig.setDriverClassName(mysql.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }
}
