package uk.gov.cslearning.catalogue.service.upload.processor;

import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;

public class DefaultFileProcessor implements FileProcessor {

    @Override
    public ProcessedFile process(FileUpload fileUpload) {
        return () -> fileUpload;
    }
}
