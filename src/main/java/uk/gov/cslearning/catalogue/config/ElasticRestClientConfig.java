package uk.gov.cslearning.catalogue.config;

import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import co.elastic.clients.transport.TransportOptions;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.module.EventStatus;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@EnableElasticsearchRepositories(basePackages = "uk.gov.cslearning.catalogue.repository.elastic")
public class ElasticRestClientConfig extends ElasticsearchConfiguration {

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
    @NotNull
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(host + ":" + port);

        ClientConfiguration.TerminalClientConfigurationBuilder terminalBuilder;
        if ("https".equalsIgnoreCase(protocol)) {
            terminalBuilder = builder.usingSsl();
        } else {
            terminalBuilder = builder;
        }

        if (username != null && !username.isBlank() && password != null && !password.isBlank()) {
            terminalBuilder = (ClientConfiguration.TerminalClientConfigurationBuilder) terminalBuilder.withBasicAuth(username, password);
        }

        return terminalBuilder
                .withSocketTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Override
    public TransportOptions transportOptions() {
        return super.transportOptions().toBuilder()
                .setHeader("Accept", "application/vnd.elasticsearch+json;compatible-with=8")
                .setHeader("Content-Type", "application/vnd.elasticsearch+json;compatible-with=8")
                .build();
    }

    @Bean
    @Override
    @NotNull
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(List.of(
                new ZonedDateTimeToStringConverter(),
                new StringToZonedDateTimeConverter(),
                new URLToStringConverter(),
                new StringToURLConverter(),
                new StatusToStringConverter(),
                new StringtoStatusConverter(),
                new EventStatusToStringConverter(),
                new StringtoEventStatusConverter()));
    }

    @WritingConverter
    public class ZonedDateTimeToStringConverter implements Converter<LocalDateTime, String> {

        @Override
        public String convert(LocalDateTime source) {
            return source.format(DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    @ReadingConverter
    public class StringToZonedDateTimeConverter implements Converter<String, LocalDateTime> {

        @Override
        public LocalDateTime convert(@NotNull String source) {
            return LocalDateTime.parse(source, DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    @WritingConverter
    public class URLToStringConverter implements Converter<URL, String> {

        @Override
        public String convert(URL source) {
            return source.toString();
        }
    }

    @ReadingConverter
    public class StringToURLConverter implements Converter<String, URL> {

        @SneakyThrows
        @Override
        @SuppressWarnings("deprecation")
        public URL convert(@NotNull String source) {
            return new URL(source);
        }
    }

    @WritingConverter
    public class StatusToStringConverter implements Converter<Status, String> {

        @Override
        public String convert(Status source) {
            return source.getValue();
        }
    }

    @ReadingConverter
    public class StringtoStatusConverter implements Converter<String, Status> {

        @SneakyThrows
        @Override
        public Status convert(@NotNull String source) {
            return Status.forValue(source);
        }
    }

    @WritingConverter
    public class EventStatusToStringConverter implements Converter<EventStatus, String> {

        @Override
        public String convert(EventStatus source) {
            return source.getValue();
        }
    }

    @ReadingConverter
    public class StringtoEventStatusConverter implements Converter<String, EventStatus> {

        @SneakyThrows
        @Override
        public EventStatus convert(@NotNull String source) {
            return EventStatus.forValue(source);
        }
    }

}
