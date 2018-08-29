package uk.gov.cslearning.catalogue.service.upload.processor;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;

@Component
public class FileProcessorFactory {
    public FileProcessor create(FileUpload fileUpload) {
        return new DefaultFileProcessor();
    }
}
