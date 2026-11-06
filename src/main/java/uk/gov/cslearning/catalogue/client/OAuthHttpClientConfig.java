package uk.gov.cslearning.catalogue.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuthHttpClientConfig {

    @Value("${record.serviceUrl}")
    private String learnerRecordBaseUrl;

    @Value("${identity.serviceUrl}")
    private String identityBaseUrl;

    @Value("${registry.serviceUrl}")
    private String csrsBaseUrl;

    private final OauthClientFactory oauthClientFactory;

    public OAuthHttpClientConfig(OauthClientFactory oauthClientFactory) {
        this.oauthClientFactory = oauthClientFactory;
    }

    @Bean(name = "learnerRecordHttpClient")
    public IHttpClient learnerRecordClient() {
        return oauthClientFactory.buildClient(learnerRecordBaseUrl);
    }

    @Bean(name = "identityHttpClient")
    IHttpClient identityOAuthClient() {
        return oauthClientFactory.buildClient(identityBaseUrl);
    }

    @Bean(name = "csrsHttpClient")
    IHttpClient csrsClient() {
        return oauthClientFactory.buildClient(csrsBaseUrl);
    }
}
