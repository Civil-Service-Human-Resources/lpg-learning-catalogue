package uk.gov.cslearning.catalogue.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private final String title;
    private final String description;
    private final String version;

    public OpenApiConfig(
            @Value("${info.app.title}") String title,
            @Value("${info.app.description}") String description,
            @Value("${info.app.version}") String version) {
        this.title = title;
        this.description = description;
        this.version = version;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version));
    }
}
