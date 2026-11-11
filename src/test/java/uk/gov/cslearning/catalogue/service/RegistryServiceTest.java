package uk.gov.cslearning.catalogue.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cslearning.catalogue.client.csrs.ICSRSClient;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.service.registry.RegistryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistryServiceTest {

    @Mock
    private ICSRSClient client;

    @InjectMocks
    private RegistryService registryService;

    @Test
    public void getCurrentCivilServant() {
        CivilServant civilServant = new CivilServant();

        when(client.getLoggedInCivilServant()).thenReturn(civilServant);

        assertEquals(civilServant, registryService.getCurrentCivilServant());
    }
}