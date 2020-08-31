package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.util.Map;

@Component
public class UploaderFactory {

    private final Map<String, Uploader> uploaderMap;

    public UploaderFactory(@Qualifier("uploaderMap") Map<String, Uploader> uploaderMap) {
        this.uploaderMap = uploaderMap;
    }

    public Uploader create(ProcessedFile processedFile) {
        String extension = processedFile.getFileUpload().getExtension().toLowerCase();

        if (uploaderMap.containsKey(extension)) {
            return uploaderMap.get(extension);
        }

        throw new UnknownFileTypeException(String.format("Uploaded file has an unknown extension: %s", extension));
    }
}
