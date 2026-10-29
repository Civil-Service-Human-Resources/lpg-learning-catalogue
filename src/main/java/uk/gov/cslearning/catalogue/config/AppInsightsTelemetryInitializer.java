package uk.gov.cslearning.catalogue.config;

import com.microsoft.applicationinsights.extensibility.TelemetryInitializer;
import com.microsoft.applicationinsights.extensibility.context.ContextTagKeys;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.telemetry.Telemetry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;

@Slf4j
@Component
public class AppInsightsTelemetryInitializer implements TelemetryInitializer {

    private static final int MAX_IP_LENGTH = 45;
    private static final int MAX_URL_LENGTH = 2048;

    @Override
    public void initialize(Telemetry telemetry) {
        if (telemetry == null) {
            return;
        }

        sanitizeLocationIp(telemetry);
        sanitizeRequestUrl(telemetry);
    }

    /* Fixing the following warnings:
       WARN  c.a.m.o.a.i.p.TelemetryPipeline - Sending telemetry to the ingestion service: Received response code 206
       (100: Field 'ai.location.ip' on type 'ContextTagKeys' is too long. Expected: 46 characters)
       (future warnings will be aggregated and logged once every 5 minutes)
       WARN  c.a.m.o.a.i.p.TelemetryPipeline - In the last 5 minutes, the following operation has failed 4 times (out of 226):
       Sending telemetry to the ingestion service:
       Received response code 206 (100: Field 'ai.location.ip' on type 'ContextTagKeys' is too long. Expected: 46 characters) (3 times)
    */
    private void sanitizeLocationIp(Telemetry telemetry) {
        if (telemetry.getContext() == null) {
            return;
        }
        Map<String, String> tags = telemetry.getContext().getTags();
        if (tags == null) {
            return;
        }
        String locationIpKey = ContextTagKeys.getKeys().getLocationIP();
        String ip = tags.get(locationIpKey);
        if (StringUtils.isBlank(ip)) {
            return;
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        if (ip.length() > MAX_IP_LENGTH) {
            ip = ip.substring(0, MAX_IP_LENGTH);
        }
        tags.put(locationIpKey, ip);
    }

    /* Fixing the following warning:
       WARN  c.a.m.o.a.i.b.TelemetryTruncation - truncated Request.url attribute value to 2048 characters
       (this message will only be logged once per attribute name):
       http://learning-resources.environment.learn.domain.abc.xy/?=//example.com&...
    */
    private void sanitizeRequestUrl(Telemetry telemetry) {
        if (!(telemetry instanceof RequestTelemetry requestTelemetry)) {
            return;
        }
        String url = requestTelemetry.getUrlString();
        if (url == null || url.length() <= MAX_URL_LENGTH) {
            return;
        }
        String truncatedUrl = url.substring(0, MAX_URL_LENGTH);
        try {
            requestTelemetry.setUrl(truncatedUrl);
        } catch (MalformedURLException e) {
            setRawUrlViaReflection(requestTelemetry, truncatedUrl);
        }
    }

    private void setRawUrlViaReflection(RequestTelemetry requestTelemetry, String url) {
        try {
            Method getDataMethod = RequestTelemetry.class.getDeclaredMethod("getData");
            getDataMethod.setAccessible(true);
            Object data = getDataMethod.invoke(requestTelemetry);
            if (data != null) {
                Method setUrlMethod = data.getClass().getMethod("setUrl", String.class);
                setUrlMethod.invoke(data, url);
            }
        } catch (Exception ex) {
            log.warn("Failed to set truncated URL on RequestTelemetry", ex);
        }
    }
}
