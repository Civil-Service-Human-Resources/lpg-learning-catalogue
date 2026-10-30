package uk.gov.cslearning.catalogue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /*
        In older versions of Spring (Spring Framework 5 / Spring Boot 2), Spring MVC enabled trailing slash matching by
        default (i.e. `/courses` and `/courses/` were treated as equivalent).
        In Spring Framework 6.0+ (Spring Boot 3+), trailing slash matching is **disabled by default** to follow
        modern RFC standards and avoid ambiguity in security configurations.
        As a result, Spring does not match `POST /courses/` to `@RequestMapping("/courses")`.
        Therefore, the request is failing with `404 Not Found` because the frontend application is sending the
        HTTP request to `POST /courses/` (with a trailing slash) instead of `POST /courses`.
        To fix the above issue, a `UrlHandlerFilter` bean is added to automatically trim trailing slashes before routing.
     */
    @Bean
    public FilterRegistrationBean<UrlHandlerFilter> urlHandlerFilter() {
        UrlHandlerFilter filter = UrlHandlerFilter.trailingSlashHandler("/**")
                .wrapRequest()
                .build();
        FilterRegistrationBean<UrlHandlerFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
