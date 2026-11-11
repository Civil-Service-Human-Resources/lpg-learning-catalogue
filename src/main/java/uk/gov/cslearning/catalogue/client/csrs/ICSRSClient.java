package uk.gov.cslearning.catalogue.client.csrs;

import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;

public interface ICSRSClient {

    CivilServant getLoggedInCivilServant();

}
