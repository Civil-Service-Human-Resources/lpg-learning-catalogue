package uk.gov.cslearning.catalogue.service.upload.processor;

import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;

public interface FileProcessor {
    ProcessedFileUpload process(FileUpload fileUpload) throws FileProcessingException;
}
