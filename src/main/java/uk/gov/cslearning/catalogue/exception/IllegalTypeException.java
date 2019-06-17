package uk.gov.cslearning.catalogue.exception;

public class IllegalTypeException extends Throwable {
    public <T> IllegalTypeException(Class<T> type) {
    }
}
