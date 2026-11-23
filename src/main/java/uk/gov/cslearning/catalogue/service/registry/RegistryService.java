package uk.gov.cslearning.catalogue.service.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.client.csrs.ICSRSClient;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;

@Slf4j
@Service
public class RegistryService {

    private final ICSRSClient client;

    public RegistryService(ICSRSClient client) {
        this.client = client;
    }

    @PreAuthorize("isAuthenticated()")
    public CivilServant getCurrentCivilServant() {
        log.debug("Getting profile details for authenticated user");
        return client.getLoggedInCivilServant();
    }
}
