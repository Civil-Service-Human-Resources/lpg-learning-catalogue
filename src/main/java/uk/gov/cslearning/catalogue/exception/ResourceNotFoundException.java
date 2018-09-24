package uk.gov.cslearning.catalogue.exception;

import java.util.NoSuchElementException;

public class ResourceNotFoundException extends NoSuchElementException {

    private ResourceNotFoundException() {
    }

    public static ResourceNotFoundException resourceNotFoundException() {
        return new ResourceNotFoundException();
    }
}