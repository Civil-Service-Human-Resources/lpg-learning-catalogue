package uk.gov.cslearning.catalogue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import uk.gov.cslearning.catalogue.evaluators.CustomPermissionEvaluator;
import uk.gov.cslearning.catalogue.service.AuthoritiesService;
import uk.gov.cslearning.catalogue.service.CourseService;
import uk.gov.cslearning.catalogue.service.RegistryService;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            @Lazy RegistryService registryService,
            @Lazy CourseService courseService,
            @Lazy AuthoritiesService authoritiesService) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator(registryService, courseService, authoritiesService));
        return expressionHandler;
    }
}
