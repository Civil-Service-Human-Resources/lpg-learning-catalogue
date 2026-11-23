package uk.gov.cslearning.catalogue.client.identity;

import uk.gov.cslearning.catalogue.client.OAuthToken;

public interface IIdentityClient {

    OAuthToken getServiceToken();
}
