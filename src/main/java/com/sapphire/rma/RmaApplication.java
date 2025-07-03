package com.sapphire.rma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.sapphire.rma.repository")
@ComponentScan(basePackages = "com.sapphire.rma")
public class RmaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RmaApplication.class, args);
    }
}