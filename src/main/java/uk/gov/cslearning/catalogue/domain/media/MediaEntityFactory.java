package uk.gov.cslearning.catalogue.domain.media;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Component
public class MediaEntityFactory {

    private Map<String, Function<Upload, MediaEntity>> createMethods = ImmutableMap.of(
            "doc", new CreateDocumentFunction()
    );

    public MediaEntity create(Upload upload) {

        if (createMethods.containsKey(upload.getProcessedFile().getFileUpload().getExtension())) {
            return createMethods.get(upload.getProcessedFile().getFileUpload().getExtension()).apply(upload);
        }

        throw new UnknownFileTypeException(String.format("Uploaded file has an unknown extension: %s",
                upload.getProcessedFile().getFileUpload().getExtension()));
    }

    private static class CreateDocumentFunction implements Function<Upload, MediaEntity> {
        @Override
        public MediaEntity apply(Upload upload) {
            Document document = new Document();
            document.setId(upload.getProcessedFile().getFileUpload().getId());
            document.setContainer(upload.getProcessedFile().getFileUpload().getContainer());
            document.setDateAdded(LocalDateTime.now(Clock.systemUTC()));
            document.setExtension(upload.getProcessedFile().getFileUpload().getExtension());
            document.setName(upload.getProcessedFile().getFileUpload().getName());
            document.setPath(upload.getPath());
            document.setFileSize(upload.getSize());

            return document;
        }
    }
}
