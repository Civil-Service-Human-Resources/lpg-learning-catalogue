package uk.gov.cslearning.catalogue.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.elasticsearch.autoconfigure.DataElasticsearchAutoConfiguration;
import org.springframework.boot.data.elasticsearch.autoconfigure.DataElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.config.SecurityConfig;
import uk.gov.cslearning.catalogue.config.SwaggerConfig;
import uk.gov.cslearning.catalogue.config.WebConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {
                DisabledOpenApiEndpointTest.TestApp.class
        },
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false",
                "identity.tokenEndpoint=/oauth2/token",
                "spring.security.oauth2.resource.jwt.key-value=test-jwt-secret-key-12345678901234567890"
        }
)
@AutoConfigureMockMvc
public class DisabledOpenApiEndpointTest {

    @SpringBootApplication(
            scanBasePackages = {},
            exclude = {
                    DataSourceAutoConfiguration.class,
                    FlywayAutoConfiguration.class,
                    HibernateJpaAutoConfiguration.class,
                    DataElasticsearchAutoConfiguration.class,
                    DataElasticsearchRepositoriesAutoConfiguration.class
            }
    )
    @Import({
            SwaggerConfig.class,
            WebConfiguration.class,
            SecurityConfig.class
    })
    static class TestApp {}

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnNotFoundForApiDocsWhenDisabled() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundForSwaggerUiWhenDisabled() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isNotFound());
    }
}
