package uk.gov.cslearning.catalogue.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.service.record.RequestEntityFactory;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class RegistryServiceTest {

    @Mock
    private RestTemplate restOperations;

    @Mock
    private RequestEntityFactory requestEntityFactory;

    @InjectMocks
    private RegistryService registryService;

    @Test
    public void getCurrentCivilServant() {
        CivilServant civilServant = new CivilServant();
        RequestEntity<Void> requestEntity = mock();
        ResponseEntity<CivilServant> responseEntity = new ResponseEntity<>(civilServant, OK);

        when(requestEntityFactory.createGetRequest((URI) any())).thenReturn(requestEntity);
        when(restOperations.exchange(requestEntity, CivilServant.class)).thenReturn(responseEntity);

        assertEquals(registryService.getCurrentCivilServant(), civilServant);
    }
}