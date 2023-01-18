package uk.gov.cslearning.catalogue.exception;

public class InvalidScormException extends RuntimeException {
    public InvalidScormException(String message) {
        super(String.format("Invalid SCORM file: %s", message));
    }
}
