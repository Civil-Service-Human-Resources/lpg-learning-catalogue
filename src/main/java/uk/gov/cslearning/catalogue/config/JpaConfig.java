package uk.gov.cslearning.catalogue.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "uk.gov.cslearning.catalogue.repository.sql")
@EntityScan(basePackages = "uk.gov.cslearning.catalogue.domain")
public class JpaConfig {
}
