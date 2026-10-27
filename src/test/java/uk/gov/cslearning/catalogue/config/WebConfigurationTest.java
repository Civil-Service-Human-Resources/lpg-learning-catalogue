package uk.gov.cslearning.catalogue.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.UrlHandlerFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WebConfigurationTest {

    private final WebConfiguration webConfiguration = new WebConfiguration();

    @Test
    public void urlHandlerFilterBeanShouldBeConfiguredWithHighestPrecedence() {
        FilterRegistrationBean<UrlHandlerFilter> registrationBean = webConfiguration.urlHandlerFilter();

        assertNotNull(registrationBean);
        assertNotNull(registrationBean.getFilter());
        assertEquals(Ordered.HIGHEST_PRECEDENCE, registrationBean.getOrder());
    }
}
