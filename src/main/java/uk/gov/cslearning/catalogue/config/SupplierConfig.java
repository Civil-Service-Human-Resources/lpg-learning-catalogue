package uk.gov.cslearning.catalogue.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@EnableAutoConfiguration
@ConfigurationProperties(prefix = "suppliers")
public class SupplierConfig {
    private Map<String, String> reportingAuthorities;

    public Map<String, String> getReportingAuthorities() {
        return reportingAuthorities;
    }

    public void setReportingAuthorities(Map<String, String> reportingAuthorities) {
        this.reportingAuthorities = reportingAuthorities;
    }
}
