package uk.gov.cslearning.catalogue.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    public void jwtAuthenticationConverterShouldExtractAuthoritiesWithoutPrefix() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        assertNotNull(converter);

        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256", "typ", "JWT"),
                Map.of(
                        "sub", "user-id-123",
                        "authorities", List.of("LEARNING_CREATE", "LEARNING_MANAGER", "CSL_AUTHOR")
                )
        );

        AbstractAuthenticationToken authenticationToken = converter.convert(jwt);
        assertNotNull(authenticationToken);

        Collection<GrantedAuthority> authorities = authenticationToken.getAuthorities();
        List<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertTrue(authorityStrings.contains("LEARNING_CREATE"));
        assertTrue(authorityStrings.contains("LEARNING_MANAGER"));
        assertTrue(authorityStrings.contains("CSL_AUTHOR"));
        assertTrue(authorityStrings.contains("FACTOR_BEARER"));
    }
}
