package uk.gov.cslearning.catalogue.integration;


import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.cslearning.catalogue.config.DisableBlobStorage;
import uk.gov.cslearning.catalogue.config.IntegrationTestConfig;
import uk.gov.cslearning.catalogue.util.DataService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ActiveProfiles("integration-test")
@RunWith(SpringRunner.class)
@Import({IntegrationTestConfig.class, DisableBlobStorage.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected DataService dataService;

    protected static String elasticImage = "docker.elastic.co/elasticsearch/elasticsearch:8.4.2";

    static ElasticsearchContainer ELASTICSEARCH_CONTAINER;
    static {
        boolean useLocalElastic = Boolean.parseBoolean(System.getenv("INTEGRATION_TEST_USE_LOCAL_ELASTIC"));
        if (!useLocalElastic) {
            log.info("useLocalElastic setting is false. Creating an elasticsearch container.");
            ELASTICSEARCH_CONTAINER = new ElasticsearchContainer(DockerImageName.parse(elasticImage));
            ELASTICSEARCH_CONTAINER.withEnv("discovery.type", "single-node");
            ELASTICSEARCH_CONTAINER.withEnv("cluster.name", "local");
            ELASTICSEARCH_CONTAINER.withEnv("xpack.security.enabled", "false");
            ELASTICSEARCH_CONTAINER.withExposedPorts(9200);
            ELASTICSEARCH_CONTAINER.withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(9200), new ExposedPort(9200)))
            ));
            ELASTICSEARCH_CONTAINER.withStartupTimeout(Duration.of(5, ChronoUnit.MINUTES));
            ELASTICSEARCH_CONTAINER.start();
        } else {
            log.info("useLocalElastic setting is true. Using a local elasticsearch instance on port 9200.");
        }
    }

    @Before
    public void beforeAll() {
        dataService.loadBulkCourses();
    }

    @PostConstruct
    public void afterAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> dataService.deleteBulkCourses()));
    }

    protected ResultActions submitRequest(RequestBuilder requestBuilder) throws Exception {
        return mvc.perform(requestBuilder)
                .andDo(result -> {
                    System.out.println("API RESPONSE");
                    String json = result.getResponse().getContentAsString();
                    System.out.println(json);
                });
    }

}
