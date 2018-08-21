package uk.gov.cslearning.catalogue.exception;

public class UnknownFileTypeException extends RuntimeException {
    public UnknownFileTypeException(String message) {
        super(message);
    }
}
