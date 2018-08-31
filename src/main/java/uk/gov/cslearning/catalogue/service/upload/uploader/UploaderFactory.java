package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class UploaderFactory {

    private final Map<String, Supplier<Uploader>> uploaderFactoryMethods;

    public UploaderFactory(@Qualifier("uploaderFactoryMethods") Map<String, Supplier<Uploader>> uploaderFactoryMethods) {
        this.uploaderFactoryMethods = uploaderFactoryMethods;
    }

    public Uploader create(ProcessedFile processedFile) {
        String extension = processedFile.getFileUpload().getExtension();

        if (uploaderFactoryMethods.containsKey(extension)) {
            return uploaderFactoryMethods.get(extension).get();
        }

        throw new UnknownFileTypeException(String.format("Uploaded file has an unknown extension: %s", extension));
    }
}
