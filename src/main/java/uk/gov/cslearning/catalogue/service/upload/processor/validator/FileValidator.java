package uk.gov.cslearning.catalogue.service.upload.processor.validator;

import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.exception.FileValidationException;

public interface FileValidator {

    void validate(FileUpload fileUpload) throws FileValidationException;
}
