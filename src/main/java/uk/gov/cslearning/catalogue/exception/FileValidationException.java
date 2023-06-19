package uk.gov.cslearning.catalogue.exception;

public class FileValidationException extends RuntimeException {

    public FileValidationException(Throwable e) {
        super(e);
    }

    public FileValidationException(String msg) {
        super(msg);
    }

    public FileValidationException(String msg, Throwable e) {
        super(msg, e);
    }
}
