package uk.gov.cslearning.catalogue.client.csrs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.exception.NotFoundException;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CSRSClientTest {

    @Mock
    private IHttpClient httpClient;

    private final String csrsGetCivilServantUrl = "/civilServants/me";

    @Test
    public void testGetLoggedInCivilServant() {
        CSRSClient client = new CSRSClient(httpClient, csrsGetCivilServantUrl);
        CivilServant civilServant = mock(CivilServant.class);
        URI uri = UriComponentsBuilder.fromUriString(csrsGetCivilServantUrl).build().toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();

        when(httpClient.executeRequest(requestEntity, CivilServant.class)).thenReturn(civilServant);

        CivilServant result = client.getLoggedInCivilServant();

        assertEquals(civilServant, result);
    }

    @Test
    public void testGetLoggedInCivilServantNotFound() {
        CSRSClient client = new CSRSClient(httpClient, csrsGetCivilServantUrl);
        URI uri = UriComponentsBuilder.fromUriString(csrsGetCivilServantUrl).build().toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();

        when(httpClient.executeRequest(requestEntity, CivilServant.class)).thenThrow(new NotFoundException("Not found"));

        CivilServant result = client.getLoggedInCivilServant();

        assertNull(result);
    }
}
