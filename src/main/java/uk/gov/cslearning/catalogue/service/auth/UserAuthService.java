package uk.gov.cslearning.catalogue.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.config.CustomOAuth2User;
import uk.gov.cslearning.catalogue.dto.IdentityDto;
import uk.gov.cslearning.catalogue.exception.ClientAuthenticationErrorException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class UserAuthService implements IUserAuthService {

    public CustomOAuth2User getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomOAuth2User customAuth) {
                return customAuth;
            }
            Jwt jwt = extractJwt(authentication);
            if (jwt != null) {
                Map<String, Object> claims = jwt.getClaims();
                String nameAttributeKey = "user_name";
                if (!claims.containsKey(nameAttributeKey)) {
                    if (claims.containsKey("sub")) {
                        nameAttributeKey = "sub";
                    } else if (claims.containsKey("uid")) {
                        nameAttributeKey = "uid";
                    } else if (!claims.isEmpty()) {
                        nameAttributeKey = claims.keySet().iterator().next();
                    }
                }
                String uid = (String) claims.get("user_name");
                if (uid == null) {
                    uid = (String) claims.get("sub");
                }
                if (uid == null) {
                    uid = (String) claims.get("uid");
                }
                String email = (String) claims.get("email");
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                return new CustomOAuth2User(
                        authorities != null ? authorities : List.of(),
                        claims,
                        nameAttributeKey,
                        uid,
                        email,
                        jwt
                );
            }
        }
        throw new ClientAuthenticationErrorException("No valid CustomOAuth2User found in security context");
    }

    @Override
    public Jwt getBearerTokenFromUserAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof CustomOAuth2User customAuth) {
            return customAuth.getToken();
        }
        return extractJwt(authentication);
    }

    private Jwt extractJwt(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        if (authentication.getCredentials() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }

    @Override
    public IdentityDto getIdentity() {
        CustomOAuth2User auth = getAuthentication();
        List<String> roles = new ArrayList<>(auth.getRoles());
        String uid = auth.getUid();
        return new IdentityDto(uid, roles);
    }
}
