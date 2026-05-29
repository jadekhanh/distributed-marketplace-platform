package com.jade.marketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * MySQL configuration enables Spring Data JPA reposositories
 * Use MySQL for main relational database for users, products, orders, inventory, reviews, payments, and notifications
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.jade.marketplace")
public class MySQLConfig {
}