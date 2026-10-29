package uk.gov.cslearning.catalogue.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SwaggerConfigTest {

    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    public void openAPIShouldReturnConfiguredInfo() {
        OpenAPI openAPI = swaggerConfig.openAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Learning Catalogue API", openAPI.getInfo().getTitle());
        assertEquals("Civil Service Learning Catalogue Service API", openAPI.getInfo().getDescription());
        assertEquals("v1", openAPI.getInfo().getVersion());
    }
}
