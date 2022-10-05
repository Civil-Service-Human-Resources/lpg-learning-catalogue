package uk.gov.cslearning.catalogue.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.HttpHeaders;

import java.net.URI;

@Configuration
@EnableElasticsearchRepositories(basePackages = "uk.gov.cslearning.catalogue.repository")
public class ElasticRestClientConfig extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.uri}")
    private URI uri;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.readTimeout}")
    private int readTimeout;


    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(uri.toString())
                .withBasicAuth(username, password)
                .withSocketTimeout(readTimeout)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
