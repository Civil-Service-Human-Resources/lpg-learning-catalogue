package uk.gov.cslearning.catalogue.exception;

public class RusticiEngineException extends RuntimeException {

    public RusticiEngineException(String msg, Throwable e) {
        super(e);
    }

    public RusticiEngineException(String msg) {
        super(msg);
    }
}
