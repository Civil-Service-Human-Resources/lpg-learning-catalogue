package uk.gov.cslearning.catalogue.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig("lpg-learning-catalogue", "Learning resource API", "1.0.0");

    @Test
    public void openAPIShouldReturnConfiguredInfo() {
        OpenAPI openAPI = openApiConfig.openAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("lpg-learning-catalogue", openAPI.getInfo().getTitle());
        assertEquals("Learning resource API", openAPI.getInfo().getDescription());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
    }
}
