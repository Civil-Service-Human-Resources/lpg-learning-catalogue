package uk.gov.cslearning.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.cslearning.catalogue.config.DefaultConfig;

/**
 * Main Spring application configuration and entry point.
 */
@SpringBootApplication
@Import(DefaultConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
