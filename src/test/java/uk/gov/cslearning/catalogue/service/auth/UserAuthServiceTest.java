package uk.gov.cslearning.catalogue.service.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.gov.cslearning.catalogue.config.CustomOAuth2User;
import uk.gov.cslearning.catalogue.dto.IdentityDto;
import uk.gov.cslearning.catalogue.exception.ClientAuthenticationErrorException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthServiceTest {

    private final UserAuthService userAuthService = new UserAuthService();

    @BeforeEach
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCustomOAuth2UserWhenPrincipalIsCustomOAuth2User() {
        Jwt jwt = createJwt("user-123", "test@example.com", List.of("ROLE_USER"));
        CustomOAuth2User customUser = new CustomOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                jwt.getClaims(),
                "user_name",
                "user-123",
                "test@example.com",
                jwt
        );

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_USER"))) {
            @Override
            public Object getPrincipal() {
                return customUser;
            }
        };
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomOAuth2User result = userAuthService.getAuthentication();
        assertSame(customUser, result);
        assertEquals("user-123", result.getUid());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(jwt, result.getToken());
    }

    @Test
    void shouldCreateCustomOAuth2UserWhenPrincipalIsJwt() {
        Jwt jwt = createJwt("user-456", "learner@gov.uk", List.of("CSL_AUTHOR", "LEARNING_MANAGER"));
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("CSL_AUTHOR"),
                new SimpleGrantedAuthority("LEARNING_MANAGER")
        );
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomOAuth2User result = userAuthService.getAuthentication();
        assertNotNull(result);
        assertEquals("user-456", result.getUid());
        assertEquals("learner@gov.uk", result.getEmail());
        assertEquals(jwt, result.getToken());
        assertEquals(Set.of("CSL_AUTHOR", "LEARNING_MANAGER"), result.getRoles());
    }

    @Test
    void shouldFallbackToSubClaimForUidWhenUserNameClaimMissing() {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", "sub-id-789", "email", "sub@gov.uk")
        );
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomOAuth2User result = userAuthService.getAuthentication();
        assertNotNull(result);
        assertEquals("sub-id-789", result.getUid());
        assertEquals("sub@gov.uk", result.getEmail());
    }

    @Test
    void shouldThrowClientAuthenticationErrorExceptionWhenSecurityContextIsEmpty() {
        assertThrows(ClientAuthenticationErrorException.class, userAuthService::getAuthentication);
    }

    @Test
    void shouldReturnBearerTokenFromUserAuthWhenJwtPrincipalPresent() {
        Jwt jwt = createJwt("user-123", "test@example.com", List.of("ROLE_USER"));
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Jwt result = userAuthService.getBearerTokenFromUserAuth();
        assertNotNull(result);
        assertEquals(jwt, result);
        assertEquals("test-token-value", result.getTokenValue());
    }

    @Test
    void shouldReturnBearerTokenFromCustomOAuth2UserPrincipal() {
        Jwt jwt = createJwt("user-123", "test@example.com", List.of("ROLE_USER"));
        CustomOAuth2User customUser = new CustomOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                jwt.getClaims(),
                "user_name",
                "user-123",
                "test@example.com",
                jwt
        );
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_USER"))) {
            @Override
            public Object getPrincipal() {
                return customUser;
            }
        };
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Jwt result = userAuthService.getBearerTokenFromUserAuth();
        assertSame(jwt, result);
    }

    @Test
    void shouldReturnNullFromBearerTokenWhenSecurityContextIsEmpty() {
        Jwt result = userAuthService.getBearerTokenFromUserAuth();
        assertNull(result);
    }

    @Test
    void shouldGetIdentityFromJwtAuthentication() {
        Jwt jwt = createJwt("user-123", "test@example.com", List.of("ROLE_USER", "CSL_AUTHOR"));
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("CSL_AUTHOR")
        );
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        IdentityDto identity = userAuthService.getIdentity();
        assertNotNull(identity);
        assertEquals("user-123", identity.getUid());
        assertTrue(identity.getRoles().containsAll(List.of("ROLE_USER", "CSL_AUTHOR")));
    }

    private Jwt createJwt(String userName, String email, List<String> authorities) {
        return new Jwt(
                "test-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256", "typ", "JWT"),
                Map.of(
                        "user_name", userName,
                        "email", email,
                        "authorities", authorities
                )
        );
    }
}
