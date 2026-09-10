package uk.gov.cslearning.catalogue.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.gov.cslearning.catalogue.domain.ErrorDtoFactory;
import uk.gov.cslearning.catalogue.dto.ErrorDto;
import uk.gov.cslearning.catalogue.exception.ForbiddenException;
import uk.gov.cslearning.catalogue.exception.GenericServerException;
import uk.gov.cslearning.catalogue.exception.ResourceNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApiExceptionHandlerTest {

    @Mock
    private ErrorDtoFactory errorDtoFactory;

    private ApiExceptionHandler apiExceptionHandler;

    @Before
    public void setUp() {
        apiExceptionHandler = new ApiExceptionHandler(errorDtoFactory);
    }

    @Test
    public void shouldHandleIllegalStateException() {
        IllegalStateException exception = new IllegalStateException("Illegal state error");

        ResponseEntity response = apiExceptionHandler.handleIllegalStateException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Illegal state error", response.getBody());
    }

    @Test
    public void shouldHandleConstraintViolationException() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        when(violation1.getMessage()).thenReturn("Second violation");
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        when(violation2.getMessage()).thenReturn("First violation");

        Set<ConstraintViolation<?>> violations = new HashSet<>(Arrays.asList(violation1, violation2));
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        ErrorDto errorDto = mock(ErrorDto.class);
        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().body(errorDto);
        List<String> sortedErrors = Arrays.asList("First violation", "Second violation");

        when(errorDtoFactory.create(HttpStatus.BAD_REQUEST, sortedErrors, "Validation error")).thenReturn(errorDto);
        when(errorDto.getAsResponseEntity()).thenReturn(expectedResponse);

        ResponseEntity<Object> response = apiExceptionHandler.handleConstraintViolationExcetpion(exception);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(errorDtoFactory).create(HttpStatus.BAD_REQUEST, sortedErrors, "Validation error");
    }

    @Test
    public void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException();

        ResponseEntity response = apiExceptionHandler.handleResourceNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldHandleMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        List<FieldError> fieldErrors = Collections.singletonList(new FieldError("objectName", "field", "defaultMessage"));

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ErrorDto errorDto = mock(ErrorDto.class);
        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().body(errorDto);

        when(errorDtoFactory.createWithErrorFields(HttpStatus.BAD_REQUEST, fieldErrors)).thenReturn(errorDto);
        when(errorDto.getAsResponseEntity()).thenReturn(expectedResponse);

        ResponseEntity<Object> response = apiExceptionHandler.handleMethodArgumentNotValid(exception);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(errorDtoFactory).createWithErrorFields(HttpStatus.BAD_REQUEST, fieldErrors);
    }

    @Test
    public void shouldHandleValidationException() {
        ValidationException exception = new ValidationException("Validation failed");

        ErrorDto errorDto = mock(ErrorDto.class);
        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().body(errorDto);

        when(errorDtoFactory.create(HttpStatus.BAD_REQUEST, "Validation failed", "Validation error")).thenReturn(errorDto);
        when(errorDto.getAsResponseEntity()).thenReturn(expectedResponse);

        ResponseEntity<Object> response = apiExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(errorDtoFactory).create(HttpStatus.BAD_REQUEST, "Validation failed", "Validation error");
    }

    @Test
    public void shouldHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Access denied message");

        ErrorDto errorDto = mock(ErrorDto.class);
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);

        when(errorDtoFactory.create(HttpStatus.FORBIDDEN, "Access denied message", "Access is denied")).thenReturn(errorDto);
        when(errorDto.getAsResponseEntity()).thenReturn(expectedResponse);

        ResponseEntity<Object> response = apiExceptionHandler.handleAccessDeniedException(exception);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(errorDtoFactory).create(HttpStatus.FORBIDDEN, "Access denied message", "Access is denied");
    }

    @Test
    public void shouldHandleForbiddenException() {
        ForbiddenException exception = new ForbiddenException();

        ErrorDto errorDto = mock(ErrorDto.class);
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);

        when(errorDtoFactory.create(HttpStatus.FORBIDDEN, exception.getMessage(), "Forbidden exception")).thenReturn(errorDto);
        when(errorDto.getAsResponseEntity()).thenReturn(expectedResponse);

        ResponseEntity<Object> response = apiExceptionHandler.handleForbiddenException(exception);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(errorDtoFactory).create(HttpStatus.FORBIDDEN, exception.getMessage(), "Forbidden exception");
    }

    @Test
    public void shouldHandleGenericServerException() {
        GenericServerException exception = new GenericServerException("Internal error occurred");

        ErrorDto errorDto = mock(ErrorDto.class);
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);

        when(errorDtoFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error occurred", "Server exception")).thenReturn(errorDto);
        when(errorDto.getAsResponseEntity()).thenReturn(expectedResponse);

        ResponseEntity<Object> response = apiExceptionHandler.handleServerException(exception);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(errorDtoFactory).create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error occurred", "Server exception");
    }
}
