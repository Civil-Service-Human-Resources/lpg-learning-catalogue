package uk.gov.cslearning.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.elasticsearch.autoconfigure.DataElasticsearchAutoConfiguration;
import org.springframework.boot.data.elasticsearch.autoconfigure.DataElasticsearchRepositoriesAutoConfiguration;

/**
 * Main Spring application configuration and entry point.
 */
@SpringBootApplication(exclude = {
        DataElasticsearchRepositoriesAutoConfiguration.class,
        DataElasticsearchAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
