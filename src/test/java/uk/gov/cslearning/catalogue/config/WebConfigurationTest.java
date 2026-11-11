package uk.gov.cslearning.catalogue.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.UrlHandlerFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class WebConfigurationTest {

    private final WebConfiguration webConfiguration = new WebConfiguration();

    @Test
    public void urlHandlerFilterBeanShouldBeConfiguredWithHighestPrecedence() {
        FilterRegistrationBean<UrlHandlerFilter> registrationBean = webConfiguration.urlHandlerFilter();

        assertNotNull(registrationBean);
        assertNotNull(registrationBean.getFilter());
        assertEquals(Ordered.HIGHEST_PRECEDENCE, registrationBean.getOrder());
    }

    @Test
    public void addResourceHandlersShouldConfigureSwaggerAndWebjarsHandlers() {
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration swaggerUiRegistration = mock(ResourceHandlerRegistration.class);
        ResourceHandlerRegistration webjarsRegistration = mock(ResourceHandlerRegistration.class);

        when(registry.addResourceHandler("swagger-ui.html")).thenReturn(swaggerUiRegistration);
        when(swaggerUiRegistration.addResourceLocations("classpath:/META-INF/resources/")).thenReturn(swaggerUiRegistration);

        when(registry.addResourceHandler("/webjars/**")).thenReturn(webjarsRegistration);
        when(webjarsRegistration.addResourceLocations("classpath:/META-INF/resources/webjars/")).thenReturn(webjarsRegistration);

        webConfiguration.addResourceHandlers(registry);

        verify(registry).addResourceHandler("swagger-ui.html");
        verify(swaggerUiRegistration).addResourceLocations("classpath:/META-INF/resources/");
        verify(registry).addResourceHandler("/webjars/**");
        verify(webjarsRegistration).addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
