package uk.gov.cslearning.catalogue.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import uk.gov.cslearning.catalogue.service.rustici.CSLToRusticiDataService;

@Configuration
public class RusticiConfig {

    @Value("${rustici.api-url}")
    private String rusticiUrl;

    @Value("${rustici.username}")
    private String rusticiUsername;

    @Value("${rustici.password}")
    private String rusticiPassword;

    @Value("${rustici.tenant}")
    private String rusticiTenant;

    @Value("${azure.scorm-cdn}")
    private String scormCdn;

    @Bean()
    public CSLToRusticiDataService getDataTranslationService() {
        return new CSLToRusticiDataService(scormCdn);
    }

    @Bean("rusticiHttpClient")
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(rusticiUrl));
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("EngineTenantName", rusticiTenant);
            return execution.execute(request, body);
        });
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(rusticiUsername, rusticiPassword));
        return restTemplate;
    }
}
