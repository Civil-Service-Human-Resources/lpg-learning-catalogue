package uk.gov.cslearning.catalogue;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * Main Spring application configuration and entry point.
 */
@SpringBootApplication(exclude = {ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Qualifier("authoritySupplierNameMap")
    public Map<String, String> authoritySupplierNameMap() {
        return ImmutableMap.of(
                "KPMG_SUPPLIER_REPORTER", "KPMG",
                "KORNFERRY_SUPPLIER_REPORTER", "Kornferry",
                "KNOWLEDGEPOOL_SUPPLIER_REPORTER", "Knowledgepool"
        );

    }
}
