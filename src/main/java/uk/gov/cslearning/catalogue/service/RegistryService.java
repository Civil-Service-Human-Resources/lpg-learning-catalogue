package uk.gov.cslearning.catalogue.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.service.record.RequestEntityFactory;

import java.net.URI;

@Service
public class RegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryService.class);

    private final RestTemplate restTemplate;
    private final URI getCurrentUrl;
    private final RequestEntityFactory requestEntityFactory;

    public RegistryService(RestTemplate restTemplate,
                           RequestEntityFactory requestEntityFactory,
                           @Value("${registry.getCurrentUrl}") URI getCurrentUrl) {
        this.restTemplate = restTemplate;
        this.requestEntityFactory = requestEntityFactory;
        this.getCurrentUrl = getCurrentUrl;
    }

    @PreAuthorize("isAuthenticated()")
    public CivilServant getCurrentCivilServant() {
        LOGGER.debug("Getting profile details for authenticated user");
        RequestEntity<Void> requestEntity = requestEntityFactory.createGetRequest(getCurrentUrl);
        ResponseEntity<CivilServant> response = restTemplate.exchange(requestEntity, CivilServant.class);
        return response.getBody();
    }
}
