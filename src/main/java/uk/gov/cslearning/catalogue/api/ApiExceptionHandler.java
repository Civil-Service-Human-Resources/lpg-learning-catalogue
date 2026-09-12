package uk.gov.cslearning.catalogue.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.cslearning.catalogue.domain.ErrorDtoFactory;
import uk.gov.cslearning.catalogue.exception.ForbiddenException;
import uk.gov.cslearning.catalogue.exception.GenericServerException;
import uk.gov.cslearning.catalogue.exception.ResourceNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final ErrorDtoFactory errorDtoFactory;

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity handleIllegalStateException(Exception e) {
        log.error("Bad Request: ", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Bad Request: ", e);
        List<String> errors = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).sorted().collect(Collectors.toList());
        return errorDtoFactory.create(BAD_REQUEST, errors, "Validation error").getAsResponseEntity();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        return errorDtoFactory.createWithErrorFields(BAD_REQUEST, result.getFieldErrors(), "Validation error").getAsResponseEntity();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex) {
        return errorDtoFactory.create(BAD_REQUEST, ex.getMessage(), "Validation error").getAsResponseEntity();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return errorDtoFactory.create(FORBIDDEN, ex.getMessage(), "Access is denied").getAsResponseEntity();
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
        return errorDtoFactory.create(FORBIDDEN, ex.getMessage(), "Forbidden exception").getAsResponseEntity();
    }

    @ExceptionHandler(GenericServerException.class)
    public ResponseEntity<Object> handleServerException(GenericServerException ex) {
        return errorDtoFactory.create(INTERNAL_SERVER_ERROR, ex.getMessage(), "Server exception").getAsResponseEntity();
    }
}
