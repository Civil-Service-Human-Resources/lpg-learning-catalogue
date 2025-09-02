package uk.gov.cslearning.catalogue.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * IntelliJ will complain that this class isn't used anywhere, but it's used in the course domain object
 */
@Getter
@Setter
@Component("esRepositoryConfiguration")
public class EsRepositoryConfiguration {
    @Value("${elasticsearch.repositories.courses.indexName}")
    private String courseIndexName;
}
