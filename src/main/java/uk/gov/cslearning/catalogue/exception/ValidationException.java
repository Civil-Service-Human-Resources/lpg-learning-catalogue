package uk.gov.cslearning.catalogue.exception;

import lombok.Getter;
import uk.gov.cslearning.catalogue.dto.ErrorDto;

@Getter
public class ValidationException extends RuntimeException {
    private final ErrorDto errorDto;

    public ValidationException(String message) {
        super(message);
        this.errorDto = null;
    }

    public ValidationException(String message, ErrorDto errorDto) {
        super(message);
        this.errorDto = errorDto;
    }
}
