package uk.gov.cslearning.catalogue.client.csrs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.exception.NotFoundException;

@Component
public class CSRSClient implements ICSRSClient {

    private final IHttpClient httpClient;
    private final String csrsGetCivilServantUrl;

    public CSRSClient(@Qualifier("csrsHttpClient") IHttpClient httpClient,
                      @Value("${registry.getLoggedInCivilServant}") String csrsGetCivilServantUrl) {
        this.httpClient = httpClient;
        this.csrsGetCivilServantUrl = csrsGetCivilServantUrl;
    }

    public CivilServant getLoggedInCivilServant() {
        RequestEntity<Void> requestEntity = RequestEntity.get(csrsGetCivilServantUrl).build();
        try {
            return httpClient.executeRequest(requestEntity, CivilServant.class);
        } catch (NotFoundException e) {
            return null;
        }
    }
}
