package uk.gov.cslearning.catalogue.domain.media.factory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.util.Map;
import java.util.function.Function;

@Component
public class MediaEntityFactory {

    private final Map<String, Function<Upload, MediaEntity>> createMethods;

    public MediaEntityFactory(@Qualifier("mediaEntityFactoryMethods") Map<String, Function<Upload, MediaEntity>> createMethods) {
        this.createMethods = createMethods;
    }

    public MediaEntity create(Upload upload) {

        if (createMethods.containsKey(upload.getProcessedFile().getFileUpload().getExtension())) {
            return createMethods.get(upload.getProcessedFile().getFileUpload().getExtension()).apply(upload);
        }

        throw new UnknownFileTypeException(String.format("Uploaded file has an unknown extension: %s",
                upload.getProcessedFile().getFileUpload().getExtension()));
    }
}
