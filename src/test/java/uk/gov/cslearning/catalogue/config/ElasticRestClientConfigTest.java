package uk.gov.cslearning.catalogue.config;

import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.module.EventStatus;

import java.net.URL;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ElasticRestClientConfigTest {

    private final ElasticRestClientConfig config = new ElasticRestClientConfig();

    @Test
    public void shouldConvertStringToURLWithSpaces() {
        ElasticRestClientConfig.StringToURLConverter converter = config.new StringToURLConverter();
        String urlString = "https://localhost:9200/lpgdevcontent/_LOnqEwDS6e1VabUKqMbgA/RYo3r_FPRsm28Dmbq9P92Q/Test MP4 File.mp4";

        URL url = converter.convert(urlString);

        assertNotNull(url);
        assertEquals(urlString, url.toString());
    }

    @Test
    public void shouldConvertURLToString() throws Exception {
        ElasticRestClientConfig.URLToStringConverter converter = config.new URLToStringConverter();
        String urlString = "https://localhost:9200/lpgdevcontent/_LOnqEwDS6e1VabUKqMbgA/RYo3r_FPRsm28Dmbq9P92Q/Test MP4 File.mp4";
        @SuppressWarnings("deprecation")
        URL url = new URL(urlString);

        String result = converter.convert(url);

        assertEquals(urlString, result);
    }

    @Test
    public void shouldConvertZonedDateTimeToStringAndBack() {
        ElasticRestClientConfig.ZonedDateTimeToStringConverter toStringConverter = config.new ZonedDateTimeToStringConverter();
        ElasticRestClientConfig.StringToZonedDateTimeConverter toDateConverter = config.new StringToZonedDateTimeConverter();

        LocalDateTime now = LocalDateTime.of(2026, 10, 29, 12, 46, 3);
        String dateString = toStringConverter.convert(now);
        LocalDateTime converted = toDateConverter.convert(dateString);

        assertEquals(now, converted);
    }

    @Test
    public void shouldConvertStatusToStringAndBack() {
        ElasticRestClientConfig.StatusToStringConverter toStringConverter = config.new StatusToStringConverter();
        ElasticRestClientConfig.StringtoStatusConverter toStatusConverter = config.new StringtoStatusConverter();

        Status status = Status.PUBLISHED;
        String statusString = toStringConverter.convert(status);
        Status converted = toStatusConverter.convert(statusString);

        assertEquals(status, converted);
    }

    @Test
    public void shouldConvertEventStatusToStringAndBack() {
        ElasticRestClientConfig.EventStatusToStringConverter toStringConverter = config.new EventStatusToStringConverter();
        ElasticRestClientConfig.StringtoEventStatusConverter toEventStatusConverter = config.new StringtoEventStatusConverter();

        EventStatus eventStatus = EventStatus.ACTIVE;
        String eventStatusString = toStringConverter.convert(eventStatus);
        EventStatus converted = toEventStatusConverter.convert(eventStatusString);

        assertEquals(eventStatus, converted);
    }

    @Test
    public void shouldCreateCustomConversions() {
        ElasticsearchCustomConversions conversions = config.elasticsearchCustomConversions();
        assertNotNull(conversions);
    }
}
