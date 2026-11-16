package uk.gov.cslearning.catalogue.service.auth;

import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.cslearning.catalogue.dto.IdentityDto;
import uk.gov.cslearning.catalogue.config.CustomOAuth2User;

public interface IUserAuthService {

    CustomOAuth2User getAuthentication();

    IdentityDto getIdentity();

    Jwt getBearerTokenFromUserAuth();
}
