package uk.gov.cslearning.catalogue.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.catalogue.exception.NotFoundException;
import uk.gov.cslearning.catalogue.exception.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpClientTest {

    @Mock
    private RestTemplate restTemplate;

    private HttpClient httpClient;

    @BeforeEach
    public void setUp() {
        httpClient = new HttpClient(restTemplate);
    }

    @Test
    public void shouldReturnResponseBodyOnSuccessfulRequest() {
        RequestEntity<Void> request = RequestEntity.get("/test").build();
        ResponseEntity<String> response = ResponseEntity.ok("success");

        when(restTemplate.exchange(request, String.class)).thenReturn(response);

        String result = httpClient.executeRequest(request, String.class);

        assertEquals("success", result);
    }

    @Test
    public void shouldReturnResponseBodyOnSuccessfulParameterizedRequest() {
        RequestEntity<Void> request = RequestEntity.get("/test-list").build();
        ParameterizedTypeReference<List<String>> typeReference = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<String>> response = ResponseEntity.ok(List.of("item1", "item2"));

        when(restTemplate.exchange(request, typeReference)).thenReturn(response);

        List<String> result = httpClient.executeRequest(request, typeReference);

        assertEquals(List.of("item1", "item2"), result);
    }

    @Test
    public void shouldThrowNotFoundExceptionOn404WithoutLoggingError() {
        RequestEntity<Void> request = RequestEntity.get("/not-found").build();
        HttpClientErrorException notFoundException = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], null);

        when(restTemplate.exchange(request, String.class)).thenThrow(notFoundException);

        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                httpClient.executeRequest(request, String.class)
        );

        assertTrue(thrown.getMessage().contains("404 NOT_FOUND"));
    }

    @Test
    public void shouldThrowValidationExceptionOn400BadRequest() {
        RequestEntity<Void> request = RequestEntity.get("/bad-request").build();
        String errorJson = "{\"status\":400,\"message\":\"Validation failed\",\"errors\":[\"Field is invalid\"]}";
        HttpClientErrorException badRequestException = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, errorJson.getBytes(), null);

        when(restTemplate.exchange(request, String.class)).thenThrow(badRequestException);

        ValidationException thrown = assertThrows(ValidationException.class, () ->
                httpClient.executeRequest(request, String.class)
        );

        assertTrue(thrown.getMessage().contains("400 BAD_REQUEST"));
        assertNotNull(thrown.getErrorDto());
        assertEquals("Validation failed", thrown.getErrorDto().getMessage());
        assertEquals(1, thrown.getErrorDto().getErrors().size());
    }

    @Test
    public void shouldThrowRuntimeExceptionOn500InternalServerError() {
        RequestEntity<Void> request = RequestEntity.get("/server-error").build();
        HttpServerErrorException serverException = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", HttpHeaders.EMPTY, new byte[0], null);

        when(restTemplate.exchange(request, String.class)).thenThrow(serverException);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                httpClient.executeRequest(request, String.class)
        );

        assertTrue(thrown.getMessage().contains("500 INTERNAL_SERVER_ERROR"));
    }
}
