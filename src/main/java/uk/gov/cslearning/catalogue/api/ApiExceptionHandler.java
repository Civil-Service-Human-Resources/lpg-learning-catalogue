package uk.gov.cslearning.catalogue.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.cslearning.catalogue.dto.ErrorDto;
import uk.gov.cslearning.catalogue.exception.ResourceNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity handleIllegalStateException(Exception e) {
        log.error("Bad Request: ", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorDto> handleConstraintViolationExcetpion(ConstraintViolationException e) {
        log.error("Bad Request: ", e);
        List<String> errors = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).sorted().collect(Collectors.toList());
        ErrorDto error = new ErrorDto(errors, 400, "Validation error");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

}
