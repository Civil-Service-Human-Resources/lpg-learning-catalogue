package uk.gov.cslearning.catalogue.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

@Data
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchProperties {

    private URI uri;

    private String username;

    private String password;

    private int readTimeout;
}
