package uk.gov.cslearning.catalogue.exception;

public class LearningProviderNotFoundException extends RuntimeException {
    public LearningProviderNotFoundException(String message) {
        super(message);
    }
}
