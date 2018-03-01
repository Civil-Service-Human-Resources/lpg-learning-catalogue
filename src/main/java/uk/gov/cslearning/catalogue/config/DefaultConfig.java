package uk.gov.cslearning.catalogue.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import uk.gov.cslearning.catalogue.Application;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = Application.class)
public class DefaultConfig {
}
