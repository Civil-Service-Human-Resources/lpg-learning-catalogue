package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;

@Component
public class FileProcessorFactory {
    public FileProcessor create(FileUpload fileUpload) {
        return new DefaultFileProcessor(fileUpload);
    }

    private static class DefaultFileProcessor implements FileProcessor {
        private final FileUpload fileUpload;

        public DefaultFileProcessor(FileUpload fileUpload) {
            this.fileUpload = fileUpload;
        }

        @Override
        public ProcessedFile process() {
            return () -> fileUpload;
        }
    }

}
