package uk.gov.cslearning.catalogue.dto;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProcessedFileFactory {
    public ProcessedFile create(FileUpload fileUpload, Map<String, String> metadata) {
        ProcessedFile processedFile = new ProcessedFile(fileUpload);
        processedFile.setMetadata(metadata);
        return processedFile;
    }
}
