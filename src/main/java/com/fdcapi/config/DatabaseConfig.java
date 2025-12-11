package com.fdcapi.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.fdcdatax.entity")
@EnableJpaRepositories(basePackages = "com.fdcdatax.repository")
public class DatabaseConfig {
}
