//package uk.gov.cslearning.catalogue.config;
//
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
//import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//import org.springframework.http.HttpHeaders;
//
//import java.net.URI;
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "uk.gov.cslearning.catalogue.repository")
//public class ElasticSearchConfig {
//
////
////    @Bean
////    @Primary
////    public ElasticsearchOperations elasticsearchTemplate(RestHighLevelClient client,
////                                                         ElasticsearchConverter elasticsearchConverter,
////                                                         SimpleElasticsearchMappingContext simpleElasticsearchMappingContext) {
////        return new JestElasticsearchTemplate(
////                jestClient);
////    }
////
////    @Bean
////    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
////
////        Resource sourceData = new ClassPathResource("data.json");
////
////        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
////        factory.setResources(new Resource[]{sourceData});
////        return factory;
////    }
//
////    @Bean
////    public JestResultsMapper jestResultsMapper(SimpleElasticsearchMappingContext mappingContext) {
////
////        EntityMapper entityMapper = new CustomEntityMapper();
////        return new DefaultJestResultsMapper(mappingContext, entityMapper);
////    }
////
////    class CustomEntityMapper implements DefaultEntityMapper {
////
////        private final ObjectMapper objectMapper;
////
////        CustomEntityMapper() {
////            super();
////
////            final ObjectMapper mapper = new ObjectMapper();
////            JavaTimeModule javaTimeModule = new JavaTimeModule();
////            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_INSTANT));
////            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_INSTANT));
////            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
////            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
////            mapper.registerModule(javaTimeModule);
////            mapper.findAndRegisterModules();
////            objectMapper = mapper;
////        }
////
////        @Override
////        public String mapToString(Object object) throws IOException {
////            return objectMapper.writeValueAsString(object);
////        }
////
////        @Override
////        public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
////            return objectMapper.readValue(source, clazz);
////        }
////    }
//}
