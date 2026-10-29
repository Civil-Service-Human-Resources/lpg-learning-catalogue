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

        if (telemetry.getContext() != null) {
            Map<String, String> tags = telemetry.getContext().getTags();
            if (tags != null) {
                String locationIpKey = ContextTagKeys.getKeys().getLocationIP();
                String ip = tags.get(locationIpKey);
                if (StringUtils.isNotBlank(ip)) {
                    if (ip.contains(",")) {
                        ip = ip.split(",")[0].trim();
                    }
                    if (ip.length() > MAX_IP_LENGTH) {
                        ip = ip.substring(0, MAX_IP_LENGTH);
                    }
                    tags.put(locationIpKey, ip);
                }
            }
        }

        if (telemetry instanceof RequestTelemetry requestTelemetry) {
            String url = requestTelemetry.getUrlString();
            if (url != null && url.length() > MAX_URL_LENGTH) {
                String truncatedUrl = url.substring(0, MAX_URL_LENGTH);
                try {
                    requestTelemetry.setUrl(truncatedUrl);
                } catch (MalformedURLException e) {
                    try {
                        Method getDataMethod = RequestTelemetry.class.getDeclaredMethod("getData");
                        getDataMethod.setAccessible(true);
                        Object data = getDataMethod.invoke(requestTelemetry);
                        if (data != null) {
                            Method setUrlMethod = data.getClass().getMethod("setUrl", String.class);
                            setUrlMethod.invoke(data, truncatedUrl);
                        }
                    } catch (Exception ex) {
                        log.warn("Failed to set truncated URL on RequestTelemetry", ex);
                    }
                }
            }
        }
    }
}
