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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = {
                OpenApiEndpointTest.TestApp.class
        },
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
                "springdoc.api-docs.enabled=true",
                "springdoc.swagger-ui.enabled=true",
                "identity.tokenEndpoint=/oauth2/token",
                "spring.security.oauth2.resource.jwt.key-value=test-jwt-secret-key-12345678901234567890"
        }
)
@AutoConfigureMockMvc
public class OpenApiEndpointTest {

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
    public void shouldReturnOpenApiJsonWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Learning Catalogue API"))
                .andExpect(jsonPath("$.info.description").value("Civil Service Learning Catalogue Service API"))
                .andExpect(jsonPath("$.info.version").value("v1"));
    }

    @Test
    public void shouldReturnOpenApiYamlWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Learning Catalogue API")));
    }

    @Test
    public void shouldReturnSwaggerConfigWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs/swagger-config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configUrl").value("/v3/api-docs/swagger-config"))
                .andExpect(jsonPath("$.url").value("/v3/api-docs"));
    }

    @Test
    public void shouldAllowAccessToSwaggerUiHtmlWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void shouldAllowAccessToSwaggerUiIndexWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }
}
