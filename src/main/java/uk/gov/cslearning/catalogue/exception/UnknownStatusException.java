package uk.gov.cslearning.catalogue.exception;

public class UnknownStatusException extends RuntimeException {
    public UnknownStatusException(String status) {
        super(String.format("Unknown Status: '%s'", status));

    }
}
