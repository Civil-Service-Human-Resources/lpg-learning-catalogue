package uk.gov.cslearning.catalogue.integration;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class ElasticContainerBase {

    private static final String image = "docker.elastic.co/elasticsearch/elasticsearch:8.4.2";
    static final ElasticsearchContainer ELASTICSEARCH_CONTAINER;

    static {
        ELASTICSEARCH_CONTAINER = new ElasticsearchContainer(DockerImageName.parse(image));
        ELASTICSEARCH_CONTAINER.withEnv("discovery.type", "single-node");
        ELASTICSEARCH_CONTAINER.withEnv("cluster.name", "local");
        ELASTICSEARCH_CONTAINER.withEnv("xpack.security.enabled", "false");
        ELASTICSEARCH_CONTAINER.withExposedPorts(9200);
        ELASTICSEARCH_CONTAINER.withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(9200), new ExposedPort(9200)))
                ));
        ELASTICSEARCH_CONTAINER.withStartupTimeout(Duration.of(5, ChronoUnit.MINUTES));
        ELASTICSEARCH_CONTAINER.start();
    }

}
