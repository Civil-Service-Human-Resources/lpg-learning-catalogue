package uk.gov.cslearning.catalogue.service.upload.processor;

import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;

public interface FileProcessor {
    ProcessedFile process(FileUpload fileUpload);
}
