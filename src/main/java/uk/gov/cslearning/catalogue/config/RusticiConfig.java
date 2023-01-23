package uk.gov.cslearning.catalogue.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RusticiConfig {

    @Value("rustici.url")
    private String rusticiUrl;

    @Value("rustici.username")
    private String rusticiUsername;

    @Value("rustici.password")
    private String rusticiPassword;

    @Value("rustici.tenant")
    private String rusticiTenant;

    @Bean("rusticiHttpClient")
    public RestTemplate getRestTemplate() {
        return new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("EngineTenantName", rusticiTenant);
            return execution.execute(request, body);
        }))
                .rootUri(rusticiUrl)
                .basicAuthentication(rusticiUsername, rusticiPassword)
                .build();
    }
}
