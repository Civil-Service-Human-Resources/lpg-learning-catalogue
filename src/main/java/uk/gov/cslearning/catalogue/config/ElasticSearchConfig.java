package uk.gov.cslearning.catalogue.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@EnableElasticsearchRepositories(basePackages = "uk.gov.cslearning.catalogue.repository")
public class ElasticSearchConfig {

    @Bean
    public Client client(ElasticSearchProperties properties) throws UnknownHostException {

        Settings.Builder builder = Settings.builder();

        if (properties.getClusterName() != null) {
            builder.put("cluster.name", properties.getClusterName());
        }
        Settings settings = builder.build();

        return new PreBuiltTransportClient(settings)
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(properties.getHost()), properties.getPort()));
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate(Client client) throws Exception {
        return new ElasticsearchTemplate(client);
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {

        Resource sourceData = new ClassPathResource("data.json");

        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(new Resource[] { sourceData });
        return factory;
    }
}
