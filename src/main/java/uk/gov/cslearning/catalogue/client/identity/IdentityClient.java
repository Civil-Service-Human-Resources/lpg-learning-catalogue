package uk.gov.cslearning.catalogue.client.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.client.OAuthToken;
import uk.gov.cslearning.catalogue.exception.InternalAuthErrorException;

import java.net.URI;

@Component
@Slf4j
public class IdentityClient implements IIdentityClient {

    private final IHttpClient client;

    @Value("${identity.tokenEndpoint}")
    private String token;

    public IdentityClient(@Qualifier("identityHttpClient") IHttpClient client) {
        this.client = client;
    }

    @Override
    public OAuthToken getServiceToken() {
        log.debug("Getting service token from identity service");
        URI uri = UriComponentsBuilder.fromUriString(token)
                .queryParam("grant_type", "client_credentials")
                .build()
                .toUri();
        RequestEntity<Void> request = RequestEntity.post(uri).build();
        OAuthToken response = client.executeRequest(request, OAuthToken.class);
        if (response == null) {
            throw new InternalAuthErrorException("Service token response was null");
        }
        return response;
    }
}
