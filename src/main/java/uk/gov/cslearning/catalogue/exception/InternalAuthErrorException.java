package uk.gov.cslearning.catalogue.exception;

public class InternalAuthErrorException extends RuntimeException {
    public InternalAuthErrorException(String message) {
        super(message);
    }
}
