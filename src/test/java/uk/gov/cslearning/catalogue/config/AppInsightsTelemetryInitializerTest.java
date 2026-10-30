package uk.gov.cslearning.catalogue.config;

import com.microsoft.applicationinsights.extensibility.context.ContextTagKeys;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.telemetry.TraceTelemetry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppInsightsTelemetryInitializerTest {

    private final AppInsightsTelemetryInitializer initializer = new AppInsightsTelemetryInitializer();
    private final String locationIpKey = ContextTagKeys.getKeys().getLocationIP();

    @Test
    public void shouldHandleNullTelemetryOrContext() {
        initializer.initialize(null);

        RequestTelemetry requestTelemetry = new RequestTelemetry();
        initializer.initialize(requestTelemetry);
        assertNull(requestTelemetry.getContext().getTags().get(locationIpKey));
    }

    @Test
    public void shouldKeepValidSingleIPv4() {
        RequestTelemetry telemetry = new RequestTelemetry();
        telemetry.getContext().getTags().put(locationIpKey, "192.168.1.1");

        initializer.initialize(telemetry);

        assertEquals("192.168.1.1", telemetry.getContext().getTags().get(locationIpKey));
    }

    @Test
    public void shouldKeepValidSingleIPv6() {
        RequestTelemetry telemetry = new RequestTelemetry();
        telemetry.getContext().getTags().put(locationIpKey, "2000:0db9:85a4:0000:0000:8a2e:0370:7335");

        initializer.initialize(telemetry);

        assertEquals("2000:0db9:85a4:0000:0000:8a2e:0370:7335", telemetry.getContext().getTags().get(locationIpKey));
    }

    @Test
    public void shouldExtractFirstIpWhenMultipleIpsPresent() {
        RequestTelemetry telemetry = new RequestTelemetry();
        telemetry.getContext().getTags().put(locationIpKey, "192.168.1.1, 192.168.1.2, 192.168.1.3");

        initializer.initialize(telemetry);

        assertEquals("192.168.1.1", telemetry.getContext().getTags().get(locationIpKey));
    }

    @Test
    public void shouldTruncateIfIpExceedsMaxCharacters() {
        TraceTelemetry telemetry = new TraceTelemetry();
        String longIp = "12345678901234567890123456789012345678901234567890";
        telemetry.getContext().getTags().put(locationIpKey, longIp);

        initializer.initialize(telemetry);

        assertEquals(45, telemetry.getContext().getTags().get(locationIpKey).length());
        assertEquals(longIp.substring(0, 45), telemetry.getContext().getTags().get(locationIpKey));
    }

    @Test
    public void shouldKeepValidRequestUrl() throws Exception {
        RequestTelemetry telemetry = new RequestTelemetry();
        String url = "https://example.com/courses/123/modules/456";
        telemetry.setUrl(url);

        initializer.initialize(telemetry);

        assertEquals(url, telemetry.getUrlString());
    }

    @Test
    public void shouldTruncateRequestUrlIfExceedsMaxCharacters() throws Exception {
        RequestTelemetry telemetry = new RequestTelemetry();
        StringBuilder sb = new StringBuilder("https://learning-resources.environment.learn.domain-abc-xyz-localhost:9001/?q=");
        while (sb.length() < 3000) {
            sb.append("abcdefghijklmnopqrstuvwxyz0123456789");
        }
        String longUrl = sb.toString();
        telemetry.setUrl(longUrl);

        initializer.initialize(telemetry);

        assertEquals(2048, telemetry.getUrlString().length());
        assertEquals(longUrl.substring(0, 2048), telemetry.getUrlString());
    }

    @Test
    public void shouldHandleNullRequestUrl() {
        RequestTelemetry telemetry = new RequestTelemetry();

        initializer.initialize(telemetry);

        assertNull(telemetry.getUrlString());
    }
}
