package uk.gov.cslearning.catalogue.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
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

    @Value("${elasticsearch.protocol}")
    private String protocol;

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private Integer port;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.readTimeout}")
    private int readTimeout;


    @Override
    @Bean(destroyMethod = "close")
    public RestHighLevelClient elasticsearchClient() {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(host, port, protocol));
        restClientBuilder
                .setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider))
                .setDefaultHeaders(compatibilityHeaders())
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setSocketTimeout(readTimeout));
        return new RestHighLevelClient(restClientBuilder);

    }

    private Header[] compatibilityHeaders() {
        return new Header[]{
                new BasicHeader(HttpHeaders.ACCEPT, "application/vnd.elasticsearch+json;compatible-with=7"),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.elasticsearch+json;compatible-with=7")
        };
    }
}
