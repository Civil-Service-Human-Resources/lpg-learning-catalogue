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
import uk.gov.cslearning.catalogue.config.OpenApiConfig;
import uk.gov.cslearning.catalogue.config.WebConfiguration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
                "spring.security.oauth2.resource.jwt.key-value=test-jwt-secret-key-12345678901234567890",
                "spring.security.user.name=custom-swagger-user",
                "spring.security.user.password=custom-swagger-pass",
                "info.app.title=Learning Catalogue API",
                "info.app.description=Civil Service Learning Catalogue Service API",
                "info.app.version=v1"
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
            OpenApiConfig.class,
            WebConfiguration.class,
            SecurityConfig.class
    })
    static class TestApp {}

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnUnauthorizedForApiDocsWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorizedForSwaggerUiWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorizedForApiDocsWhenInvalidBasicAuth() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(httpBasic("wrong-user", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnNotFoundForApiDocsWhenDisabledAndBasicAuthenticated() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundForSwaggerUiWhenDisabledAndBasicAuthenticated() throws Exception {
        mockMvc.perform(get("/swagger-ui.html").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundForApiDocsWhenDisabledAndJwtAuthenticated() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundForSwaggerUiWhenDisabledAndJwtAuthenticated() throws Exception {
        mockMvc.perform(get("/swagger-ui.html").with(jwt()))
                .andExpect(status().isNotFound());
    }
}
