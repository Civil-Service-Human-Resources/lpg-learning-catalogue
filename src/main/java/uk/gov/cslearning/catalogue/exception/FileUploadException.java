package uk.gov.cslearning.catalogue.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(Throwable cause) {
        super(cause);
    }

    public FileUploadException (String message) {
        super(message);
    }
}
