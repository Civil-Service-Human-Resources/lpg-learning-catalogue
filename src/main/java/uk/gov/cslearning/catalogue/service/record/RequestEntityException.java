package uk.gov.cslearning.catalogue.service.record;

public class RequestEntityException extends RuntimeException {
    public RequestEntityException(Throwable cause) {
        super("Unable to create request", cause);
    }
}