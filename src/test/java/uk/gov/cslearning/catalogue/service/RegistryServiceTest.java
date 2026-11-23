package uk.gov.cslearning.catalogue.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.service.record.RequestEntityFactory;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class RegistryServiceTest {

    @Mock
    private OAuth2RestOperations restOperations;

    @Mock
    private RequestEntityFactory requestEntityFactory;

    @InjectMocks
    private RegistryService registryService;

    @Test
    public void getCurrentCivilServant() {
        CivilServant civilServant = new CivilServant();
        RequestEntity requestEntity = mock(RequestEntity.class);
        ResponseEntity responseEntity = new ResponseEntity<>(civilServant, OK);

        when(requestEntityFactory.createGetRequest((URI) any())).thenReturn(requestEntity);
        when(restOperations.exchange(requestEntity, CivilServant.class)).thenReturn(responseEntity);

        assertEquals(registryService.getCurrentCivilServant(), civilServant);
    }
}