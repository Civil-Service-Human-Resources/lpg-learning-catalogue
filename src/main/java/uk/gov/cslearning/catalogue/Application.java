package uk.gov.cslearning.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

/**
 * Main Spring application configuration and entry point.
 */
@SpringBootApplication(exclude = {
        ElasticsearchRepositoriesAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class,
        org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration.class, // Disables experimental Jackson 3
        LiquibaseAutoConfiguration.class // Exclude Liquibase to prevent the ClassNotFoundException
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
