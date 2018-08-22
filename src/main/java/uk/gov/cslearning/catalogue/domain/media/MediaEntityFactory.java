package uk.gov.cslearning.catalogue.domain.media;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Component
public class MediaEntityFactory {

    private Map<String, Function<FileUpload, MediaEntity>> createMethods = ImmutableMap.of(
            "doc", new CreateDocumentFunction()
    );

    public MediaEntity create(FileUpload fileUpload) {

        if (createMethods.containsKey(fileUpload.getExtension())) {
            return createMethods.get(fileUpload.getExtension()).apply(fileUpload);
        }

        throw new UnknownFileTypeException(String.format("Uploaded file has an unknown extension: %s", fileUpload.getExtension()));
    }

    private static class CreateDocumentFunction implements Function<FileUpload, MediaEntity> {
        @Override
        public MediaEntity apply(FileUpload fileUpload) {
            Document document = new Document();
            document.setContainer(fileUpload.getContainer());
            document.setDateAdded(LocalDateTime.now());
            document.setExtension(fileUpload.getExtension());
            document.setName(fileUpload.getName());
            document.setPath(String.join("/", fileUpload.getContainer(), fileUpload.getName()));
            document.setFileSize(fileUpload.getSize());

            return document;
        }
    }
}
