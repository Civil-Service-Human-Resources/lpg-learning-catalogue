package uk.gov.cslearning.catalogue.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.vanroy.springdata.jest.JestElasticsearchTemplate;
import com.github.vanroy.springdata.jest.mapper.DefaultJestResultsMapper;
import com.github.vanroy.springdata.jest.mapper.JestResultsMapper;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableElasticsearchRepositories(basePackages = "uk.gov.cslearning.catalogue.repository")
public class ElasticSearchConfig {

    @Bean
    public JestClient jestClient(ElasticSearchProperties properties) {
        JestClientFactory factory = new JestClientFactory();

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(properties.getUri().toString()).defaultCredentials(properties.getUsername(), properties.getPassword())
                .multiThreaded(true)
                .build());
        return factory.getObject();
    }

    @Bean
    @Primary
    public ElasticsearchOperations elasticsearchTemplate(JestClient jestClient,
                                                         ElasticsearchConverter elasticsearchConverter,
                                                         SimpleElasticsearchMappingContext simpleElasticsearchMappingContext) {
        return new JestElasticsearchTemplate(
                jestClient,
                elasticsearchConverter,
                new DefaultJestResultsMapper(simpleElasticsearchMappingContext, new CustomEntityMapper()));
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {

        Resource sourceData = new ClassPathResource("data.json");

        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(new Resource[]{sourceData});
        return factory;
    }

    @Bean
    public JestResultsMapper jestResultsMapper(SimpleElasticsearchMappingContext mappingContext) {

        EntityMapper entityMapper = new CustomEntityMapper();
        return new DefaultJestResultsMapper(mappingContext, entityMapper);
    }

    class CustomEntityMapper implements EntityMapper {

        private final ObjectMapper objectMapper;

        CustomEntityMapper(){
            super();

            final ObjectMapper mapper = new ObjectMapper();
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_INSTANT));
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_INSTANT));
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(javaTimeModule);
            mapper.findAndRegisterModules();
            objectMapper = mapper;
        }

        @Override
        public String mapToString(Object object) throws IOException {
            return objectMapper.writeValueAsString(object);
        }

        @Override
        public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
            return objectMapper.readValue(source, clazz);
        }
    }
}
