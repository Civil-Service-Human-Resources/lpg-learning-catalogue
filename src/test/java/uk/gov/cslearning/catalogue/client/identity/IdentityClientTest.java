package uk.gov.cslearning.catalogue.client.identity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.RequestEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.client.OAuthToken;
import uk.gov.cslearning.catalogue.exception.InternalAuthErrorException;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdentityClientTest {

    @Mock
    private IHttpClient client;

    @InjectMocks
    private IdentityClient identityClient;

    private final String tokenEndpoint = "/oauth2/token";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(identityClient, "token", tokenEndpoint);
    }

    @Test
    public void testGetServiceTokenSuccess() {
        OAuthToken tokenResponse = mock(OAuthToken.class);
        URI uri = UriComponentsBuilder.fromUriString(tokenEndpoint)
                .queryParam("grant_type", "client_credentials")
                .build()
                .toUri();
        RequestEntity<Void> request = RequestEntity.post(uri).build();

        when(client.executeRequest(request, OAuthToken.class)).thenReturn(tokenResponse);

        OAuthToken result = identityClient.getServiceToken();

        assertEquals(tokenResponse, result);
    }

    @Test
    public void testGetServiceTokenThrowsExceptionWhenResponseNull() {
        URI uri = UriComponentsBuilder.fromUriString(tokenEndpoint)
                .queryParam("grant_type", "client_credentials")
                .build()
                .toUri();
        RequestEntity<Void> request = RequestEntity.post(uri).build();

        when(client.executeRequest(request, OAuthToken.class)).thenReturn(null);

        assertThrows(InternalAuthErrorException.class, () -> identityClient.getServiceToken());
    }
}
