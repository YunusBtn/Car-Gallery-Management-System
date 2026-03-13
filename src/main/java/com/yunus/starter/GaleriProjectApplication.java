package com.yunus.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.yunus"})
@EntityScan(basePackages = {"com.yunus"})
@EnableJpaRepositories(basePackages = {"com.yunus.repository"})
public class GaleriProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaleriProjectApplication.class, args);
    }

}
