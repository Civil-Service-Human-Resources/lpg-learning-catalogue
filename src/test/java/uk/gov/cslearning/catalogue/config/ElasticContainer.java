package uk.gov.cslearning.catalogue.config;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Configuration
public abstract class ElasticContainer {

    @Value("${test.integration.elasticImage}")
    private String image;

    @Value("${test.integration.useLocalElastic}")
    private boolean useLocalElastic;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    void elasticsearchContainer() {
        if (!useLocalElastic) {
            log.info("useLocalElastic setting is false. Creating an elasticsearch container.");
            ElasticsearchContainer container = new ElasticsearchContainer(DockerImageName.parse(image));
            container.withEnv("discovery.type", "single-node");
            container.withEnv("cluster.name", "local");
            container.withEnv("xpack.security.enabled", "false");
            container.withExposedPorts(9200);
            container.withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                            new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(9200), new ExposedPort(9200)))
                    ));
            container.withStartupTimeout(Duration.of(5, ChronoUnit.MINUTES));
            container.start();
        } else {
            log.info("useLocalElastic setting is true. Using a local elasticsearch instance on port 9200.");
        }
    }
}
