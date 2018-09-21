package uk.gov.cslearning.catalogue.service.upload.processor;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;

@Component
public class DefaultFileProcessor implements FileProcessor {
    @Override
    public ProcessedFile process(FileUpload fileUpload) {
        return new ProcessedFile(fileUpload);
    }
}
