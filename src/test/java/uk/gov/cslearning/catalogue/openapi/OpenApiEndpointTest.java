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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
                "spring.security.oauth2.resource.jwt.key-value=test-jwt-secret-key-12345678901234567890",
                "spring.security.user.name=custom-swagger-user",
                "spring.security.user.password=custom-swagger-pass",
                "info.app.title=Learning Catalogue API",
                "info.app.description=Civil Service Learning Catalogue Service API",
                "info.app.version=v1"
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
            OpenApiConfig.class,
            WebConfiguration.class,
            SecurityConfig.class
    })
    static class TestApp {}

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectUnauthenticatedAccessToOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRejectInvalidBasicAuthAccessToOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(httpBasic("wrong-user", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRejectUnauthenticatedAccessToOpenApiYaml() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRejectUnauthenticatedAccessToSwaggerConfig() throws Exception {
        mockMvc.perform(get("/v3/api-docs/swagger-config"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRejectUnauthenticatedAccessToSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRejectUnauthenticatedAccessToSwaggerUiHtml() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRejectUnauthenticatedAccessToSwaggerUiIndex() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnOpenApiJsonWithBasicAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Learning Catalogue API"))
                .andExpect(jsonPath("$.info.description").value("Civil Service Learning Catalogue Service API"))
                .andExpect(jsonPath("$.info.version").value("v1"));
    }

    @Test
    public void shouldReturnOpenApiYamlWithBasicAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Learning Catalogue API")));
    }

    @Test
    public void shouldReturnSwaggerConfigWithBasicAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs/swagger-config").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configUrl").value("/v3/api-docs/swagger-config"))
                .andExpect(jsonPath("$.url").value("/v3/api-docs"));
    }

    @Test
    public void shouldAllowAccessToSwaggerUiWithBasicAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    public void shouldAllowAccessToSwaggerUiHtmlWithBasicAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui.html").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void shouldAllowAccessToSwaggerUiIndexWithBasicAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void shouldReturnOpenApiJsonWithOAuth2Authentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Learning Catalogue API"))
                .andExpect(jsonPath("$.info.description").value("Civil Service Learning Catalogue Service API"))
                .andExpect(jsonPath("$.info.version").value("v1"));
    }

    @Test
    public void shouldReturnOpenApiYamlWithOAuth2Authentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Learning Catalogue API")));
    }

    @Test
    public void shouldReturnSwaggerConfigWithOAuth2Authentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs/swagger-config").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configUrl").value("/v3/api-docs/swagger-config"))
                .andExpect(jsonPath("$.url").value("/v3/api-docs"));
    }

    @Test
    public void shouldAllowAccessToSwaggerUiWithOAuth2Authentication() throws Exception {
        mockMvc.perform(get("/swagger-ui").with(jwt()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    public void shouldAllowAccessToSwaggerUiHtmlWithOAuth2Authentication() throws Exception {
        mockMvc.perform(get("/swagger-ui.html").with(jwt()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void shouldAllowAccessToSwaggerUiIndexWithOAuth2Authentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void shouldRejectBasicAuthenticationOnNonOpenApiEndpoints() throws Exception {
        mockMvc.perform(get("/courses").with(httpBasic("custom-swagger-user", "custom-swagger-pass")))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", containsString("Bearer")));
    }
}
