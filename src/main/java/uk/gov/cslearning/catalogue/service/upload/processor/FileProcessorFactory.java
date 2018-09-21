package uk.gov.cslearning.catalogue.service.upload.processor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.util.Map;

@Component
public class FileProcessorFactory {

    private final Map<String, FileProcessor> fileProcessorMap;

    public FileProcessorFactory(@Qualifier("fileProcessorMap") Map<String, FileProcessor> fileProcessorMap) {
        this.fileProcessorMap = fileProcessorMap;
    }

    public FileProcessor create(FileUpload fileUpload) {
        if (fileProcessorMap.containsKey(fileUpload.getExtension())) {
            return fileProcessorMap.get(fileUpload.getExtension());
        }

        throw new UnknownFileTypeException(String.format("Uploaded file has an unknown extension: %s %s",
                fileUpload.getExtension(), fileUpload));
    }
}
