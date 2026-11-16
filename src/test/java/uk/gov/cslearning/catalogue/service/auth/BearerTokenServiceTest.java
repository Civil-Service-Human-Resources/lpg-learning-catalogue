package uk.gov.cslearning.catalogue.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cslearning.catalogue.client.OAuthToken;
import uk.gov.cslearning.catalogue.client.OAuthTokenService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BearerTokenServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-11-09T12:00:00Z"), ZoneId.of("UTC"));

    @Mock
    private OAuthTokenService oAuthTokenService;

    @Mock
    private IUserAuthService userAuthService;

    private BearerTokenService bearerTokenService;

    @BeforeEach
    void setUp() {
        bearerTokenService = new BearerTokenService(clock, oAuthTokenService, userAuthService);
        ReflectionTestUtils.setField(bearerTokenService, "refreshServiceTokenCacheBeforeSecondsToExpire", 60L);
    }

    @Test
    void shouldReturnUserBearerTokenWhenUserAuthIsPresent() {
        Jwt jwt = new Jwt(
                "user-jwt-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("user_name", "user-123")
        );
        when(userAuthService.getBearerTokenFromUserAuth()).thenReturn(jwt);

        String token = bearerTokenService.getBearerToken();

        assertEquals("user-jwt-token-value", token);
        verifyNoInteractions(oAuthTokenService);
    }

    @Test
    void shouldFallbackToServiceTokenWhenUserAuthIsNull() {
        when(userAuthService.getBearerTokenFromUserAuth()).thenReturn(null);

        OAuthToken serviceToken = new OAuthToken();
        serviceToken.setAccessToken("service-token-value");
        serviceToken.setExpiryDateTime(LocalDateTime.now(clock).plusHours(1));
        when(oAuthTokenService.getCachedOAuthServiceToken()).thenReturn(serviceToken);

        String token = bearerTokenService.getBearerToken();

        assertEquals("service-token-value", token);
        verify(oAuthTokenService).getCachedOAuthServiceToken();
    }

    @Test
    void shouldRefreshTokenWhenServiceTokenNearExpiry() {
        when(userAuthService.getBearerTokenFromUserAuth()).thenReturn(null);

        OAuthToken expiringToken = new OAuthToken();
        expiringToken.setAccessToken("old-service-token");
        expiringToken.setExpiryDateTime(LocalDateTime.now(clock).plusSeconds(30)); // < 60s

        OAuthToken refreshedToken = new OAuthToken();
        refreshedToken.setAccessToken("new-service-token");
        refreshedToken.setExpiryDateTime(LocalDateTime.now(clock).plusHours(1));

        when(oAuthTokenService.getCachedOAuthServiceToken())
                .thenReturn(expiringToken)
                .thenReturn(refreshedToken);

        String token = bearerTokenService.getBearerToken();

        assertEquals("new-service-token", token);
        verify(oAuthTokenService).removeServiceTokenFromCache();
        verify(oAuthTokenService, times(2)).getCachedOAuthServiceToken();
    }
}
