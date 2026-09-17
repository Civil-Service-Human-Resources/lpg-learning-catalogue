package uk.gov.cslearning.catalogue.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomValidationException extends RuntimeException {

    private final List<String> errors;

    public CustomValidationException(List<String> errors) {
        this.errors = errors != null ? new ArrayList<>(errors) : Collections.emptyList();
    }

    public CustomValidationException(String error) {
        this(Collections.singletonList(error));
    }

    public CustomValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors != null ? new ArrayList<>(errors) : Collections.emptyList();
    }
}
