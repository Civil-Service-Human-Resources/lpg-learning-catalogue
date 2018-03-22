package uk.gov.cslearning.catalogue.config;

import com.github.vanroy.springdata.jest.JestElasticsearchTemplate;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
@EnableElasticsearchRepositories(basePackages = "uk.gov.cslearning.catalogue.repository")
public class ElasticSearchConfig {


    @Bean
    public JestClient jestClient(ElasticSearchProperties properties) {
        JestClientFactory factory = new JestClientFactory();

        String serverUri = "http://" + properties.getHost() + ":" + String.valueOf(properties.getPort());

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(serverUri).defaultCredentials(properties.getUsername(), properties.getPassword())
                .multiThreaded(true)
                .build());
        return factory.getObject();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate(JestClient client) throws Exception {
        return new JestElasticsearchTemplate(client);
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {

        Resource sourceData = new ClassPathResource("data.json");

        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(new Resource[]{sourceData});
        return factory;
    }
}
