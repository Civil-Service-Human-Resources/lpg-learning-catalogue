package uk.gov.cslearning.catalogue.exception;

public class ClientAuthenticationErrorException extends RuntimeException {
    public ClientAuthenticationErrorException(String message) {
        super(message);
    }
}
